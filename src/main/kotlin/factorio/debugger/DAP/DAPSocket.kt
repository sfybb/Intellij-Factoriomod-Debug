package factorio.debugger.DAP

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import factorio.debugger.DAP.messages.DAPEventNames
import factorio.debugger.DAP.messages.DAPProtocolMessage
import factorio.debugger.DAP.messages.events.DAPEvent
import factorio.debugger.DAP.messages.events.DAPOutputEvent
import factorio.debugger.DAP.messages.events.DAPOutputEvent.OutputEventBody
import factorio.debugger.DAP.messages.requests.DAPCancelRequest
import factorio.debugger.DAP.messages.requests.DAPCancelRequest.CancelRequestArguments
import factorio.debugger.DAP.messages.requests.DAPRequest
import factorio.debugger.DAP.messages.requests.DAPTerminateRequest
import factorio.debugger.DAP.messages.responses.DAPResponse
import org.apache.commons.collections.buffer.CircularFifoBuffer
import org.jetbrains.annotations.Async
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.collectResults
import org.jetbrains.concurrency.rejectedPromise
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.CancellationException
import java.util.function.Consumer

class DAPSocket(outputStream: OutputStream?) : ProcessListener {
    private val logger = Logger.getInstance(DAPSocket::class.java)
    private val myObjectMapper: ObjectMapper
    private val myOutputStream: OutputStream?
    private var cur_send_sequence: Int
    private val cur_receive_sequence: Int
    private val myDAPParser: DAPMessageParser
    private val openPromises: MutableMap<Int, AsyncPromise<DAPResponse>>
    private val eventHandlers: MutableMap<DAPEventNames, Consumer<DAPEvent>>
    private val messageBodyBuilder: StringBuilder? = null
    private val lastMessages: CircularFifoBuffer
    private var myCancelRequestEnabled = false
    private var requestedTermination = false
    private val myActiveEvents: MutableMap<Int, AsyncPromise<Boolean>>

    init {
        myActiveEvents = HashMap()
        myObjectMapper = jacksonObjectMapper()
        myObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        myOutputStream = outputStream
        cur_receive_sequence = 1
        cur_send_sequence = cur_receive_sequence
        myDAPParser = DAPMessageParser()
        lastMessages = CircularFifoBuffer(20)
        openPromises = HashMap()
        eventHandlers = HashMap()
    }

    val prettyPrinter: ObjectWriter
        get() = myObjectMapper.writerWithDefaultPrettyPrinter()

    fun printLastMessages() {
        val iter: Iterator<*> = lastMessages.iterator()
        var curMsg = -lastMessages.size
        while (iter.hasNext()) {
            val o = iter.next()!!
            curMsg++
            try {
                logger.info("Message $curMsg: ${prettyPrinter.writeValueAsString(o)}")
            } catch (e: JsonProcessingException) {
                logger.info("Message $curMsg: error: ${e.message}")
            }
        }
    }

    fun setEventHandler(eventName: DAPEventNames, consumer: Consumer<DAPEvent>) {
        eventHandlers[eventName] = consumer
    }

    fun <D, R : DAPResponse> sendRequest(request: DAPRequest<D>): Promise<R> {
        var success = false
        if (myOutputStream != null) {
            success = sendRequestInternal(request, cur_send_sequence++)
        }
        if (!success) {
            return rejectedPromise()
        }
        var promise = AsyncPromise<DAPResponse>()
        openPromises[request.sequence] = promise
        if (myCancelRequestEnabled && request !is DAPCancelRequest) {
            promise = addCancellationHandler(promise, request.sequence)
        }
        return promise.then { response: DAPResponse -> response as R }
    }

    private fun addCancellationHandler(promise: AsyncPromise<DAPResponse>, request_seq: Int): AsyncPromise<DAPResponse> {
        return promise.onError { err: Throwable? ->
            if (err is CancellationException) {
                logger.info("Sending cancel request for $request_seq")
                sendRequest<CancelRequestArguments, DAPResponse>(DAPCancelRequest(request_seq))
            }
        }
    }

    protected fun <D> sendRequestInternal(
        request: DAPRequest<D>,
        @Async.Schedule sequence: Int
    ): Boolean {
        requestedTermination = requestedTermination or (request is DAPTerminateRequest)
        printMessageDebug(request)
        try {
            request.sequence = sequence
            val baos = ByteArrayOutputStream()
            val content = myObjectMapper.writeValueAsBytes(request)

            // Header
            baos.write(DAPMessageParser.contentLengthHeader.toByteArray())
            baos.write(Integer.toString(content.size).toByteArray())
            baos.write("\r\n".toByteArray())
            baos.write("\r\n".toByteArray())

            // Body
            baos.write(content)

            // only add message if sending was successful
            lastMessages.add(request)
            //logger.info("To FMTK: %s%d\n\n%s", contentLengthHeader, content.length, new String(content, StandardCharsets.UTF_8)));
            myOutputStream!!.write(baos.toByteArray())
            myOutputStream.flush()


            //logger.info(getPrettyPrinter().writeValueAsString(request));
        } catch (e: IOException) {
            if (requestedTermination) {
                // ignore this
            } else {
                logger.warn("Failed to send DAP request '$request'", e)
                return false
            }
        }
        return true
    }

    private fun printMessageDebug(message: DAPProtocolMessage) {
        val consumer = eventHandlers[DAPEventNames.OUTPUT]
        consumer?.accept(createNewDebugOutput(message))
    }

    private fun createNewDebugOutput(message: DAPProtocolMessage): DAPEvent {
        val myDebugEvent = DAPOutputEvent()
        myDebugEvent.body = OutputEventBody()
        myDebugEvent.body.category = DAPOutputEvent.OutputCategory.TELEMETRY
        if (message is DAPEvent || message is DAPResponse) {
            myDebugEvent.body.output = "---> "
        } else {
            myDebugEvent.body.output = "<--- "
        }
        try {
            myDebugEvent.body.output += myObjectMapper.writeValueAsString(message)
        } catch (e: JsonProcessingException) {
            myDebugEvent.body.output += message
        }
        return myDebugEvent
    }

    fun whenPreviousEventsProcessed(receive_sequence: Int): Promise<List<Boolean>> {
        val prevActiveEvents: MutableList<AsyncPromise<Boolean>> = ArrayList()
        synchronized(myActiveEvents) {
            for ((key, value) in myActiveEvents) {
                if (receive_sequence > key) prevActiveEvents.add(value)
            }
        }
        return prevActiveEvents.collectResults()
    }

    protected fun processEvent(event: DAPEvent) {
        val eventNames = event.eventId
        if (eventNames != null) {
            val consumer = eventHandlers[eventNames]
            if (consumer != null) {
                try {
                    consumer.accept(event)
                } catch (e: Throwable) {
                    logger.error("Encountered exception while processing event $event", e)
                }
            } else logger.info("Received unhandled $event")
        }
        synchronized(myActiveEvents) {
            val prom = myActiveEvents.remove(event.sequence)!!
            prom.setResult(java.lang.Boolean.TRUE)
        }
    }

    protected fun processResponse(
        response: DAPResponse,
        @Async.Execute sequence: Int
    ) {
        val promise = openPromises.remove(sequence)
        if (promise != null) {
            if (response.success) promise.setResult(response) else if ("cancelled" == response.message) promise.cancel() else {
                try {
                    logger.warn(ObjectMapper().writeValueAsString(response))
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }
                promise.setError(response.message!!)
            }
        } else {
            logger.info("Received unhandled $response")
        }
    }

    protected fun scheduleProcessing(message: DAPProtocolMessage) {
        lastMessages.add(message)
        printMessageDebug(message)
        ApplicationManager.getApplication().executeOnPooledThread {
            if (message is DAPResponse) {
                processResponse(message, message.requestSequence)
            } else if (message is DAPEvent) {
                synchronized(myActiveEvents) { myActiveEvents.put(message.sequence, AsyncPromise()) }
                processEvent(message)
            } else {
                logger.info("Unknown DAP message: $message")
                // TODO
            }
        }
    }

    fun setCancelRequest(cancelRequestEnabled: Boolean) {
        myCancelRequestEnabled = cancelRequestEnabled
    }

    override fun startNotified(event: ProcessEvent) {}
    override fun processTerminated(event: ProcessEvent) {}
    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        val text = event.text
        lastMessages.add(text)
        val myNewMessages = myDAPParser.parse(text)
        for (message in myNewMessages) {
            scheduleProcessing(message)
        }
    }

    fun wasTerminationRequested(): Boolean {
        return requestedTermination
    }

    val lastReceivedMessage: String
        get() {
            val it: Iterator<*> = lastMessages.iterator()
            for (i in 0 until lastMessages.size - 1) {
                it.next()
            }
            return if (it.hasNext()) it.next().toString() else ""
        }

    fun setTerminating() {
        requestedTermination = true
    }
}
