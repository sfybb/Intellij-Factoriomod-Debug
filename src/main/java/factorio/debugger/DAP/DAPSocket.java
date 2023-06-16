package factorio.debugger.DAP;

import static org.jetbrains.concurrency.Promises.collectResults;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.DAPEventNames;
import factorio.debugger.DAP.messages.DAPProtocolMessage;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.events.DAPOutputEvent;
import factorio.debugger.DAP.messages.requests.DAPCancelRequest;
import factorio.debugger.DAP.messages.requests.DAPTerminateRequest;

public class DAPSocket implements ProcessListener {
    private final Logger logger = Logger.getInstance(DAPSocket.class);
    private final ObjectMapper myObjectMapper;
    private final OutputStream myOutputStream;

    private int cur_send_sequence;
    private int cur_receive_sequence;

    private final @NotNull DAPMessageParser myDAPParser;


    private final Map<Integer, AsyncPromise<DAPResponse>> openPromises;

    private final Map<DAPEventNames, Consumer<DAPEvent>> eventHandlers;

    private StringBuilder messageBodyBuilder;

    private final CircularFifoBuffer lastMessages;
    private boolean myCancelRequestEnabled;
    private boolean requestedTermination;

    private final Map<Integer, AsyncPromise<Boolean>> myActiveEvents;


    public DAPSocket(OutputStream outputStream) {
        myActiveEvents = new HashMap<>();
        myCancelRequestEnabled = false;
        requestedTermination = false;
        myObjectMapper = new ObjectMapper();
        myObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        myOutputStream = outputStream;

        cur_send_sequence = cur_receive_sequence = 1;
        myDAPParser = new DAPMessageParser();
        lastMessages = new CircularFifoBuffer(20);

        openPromises = new HashMap<>();
        eventHandlers = new HashMap<>();
    }

    public ObjectWriter getPrettyPrinter() {
        return myObjectMapper.writerWithDefaultPrettyPrinter();
    }

    public void printLastMessages() {
        Iterator iter = lastMessages.iterator();
        int curMsg = -lastMessages.size();
        while(iter.hasNext()) {
            Object o = iter.next();
            curMsg++;
            try {
                logger.info(String.format("Message %d: %s", curMsg,
                    getPrettyPrinter().writeValueAsString(o)));
            } catch (JsonProcessingException e) {
                logger.info(String.format("Message %d: error: %s", curMsg, e.getMessage()));
            }
        }
    }

    public void setEventHandler(DAPEventNames eventName, Consumer<DAPEvent> consumer) {
        eventHandlers.put(eventName, consumer);
    }


    public<D, R extends DAPResponse> Promise<R> sendRequest(@NotNull DAPRequest<D> request) {
        boolean success = false;
        if(myOutputStream != null) {
            success = sendRequestInternal(request, cur_send_sequence++);
        }

        if(!success) {
            return Promises.rejectedPromise();
        }

        AsyncPromise<DAPResponse> promise = new AsyncPromise<>();
        openPromises.put(request.sequence, promise);
        if (myCancelRequestEnabled && !(request instanceof DAPCancelRequest)) {
            promise = addCancellationHandler(promise, request.sequence);
        }
        return promise.then(response -> (R)response);
    }

    private AsyncPromise<DAPResponse> addCancellationHandler(final AsyncPromise<DAPResponse> promise, int request_seq) {
        return promise.onError(err -> {
            if(err instanceof CancellationException) {
                logger.info(String.format("Sending cancel request for %d", request_seq));
                sendRequest(new DAPCancelRequest(request_seq));
            }
        });
    }

    protected <D> boolean sendRequestInternal(@NotNull DAPRequest<D> request,
                                              @Async.Schedule @NotNull Integer sequence) {

        requestedTermination |= request instanceof DAPTerminateRequest;

        printMessageDebug(request);

        try {
            request.sequence = sequence;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] content = myObjectMapper.writeValueAsBytes(request);

            // Header
            baos.write(DAPMessageParser.contentLengthHeader.getBytes());
            baos.write(Integer.toString(content.length).getBytes());
            baos.write("\r\n".getBytes());
            baos.write("\r\n".getBytes());

            // Body
            baos.write(content);

            // only add message if sending was successful
            lastMessages.add(request);
            //logger.info(String.format("To FMTK: %s%d\n\n%s", contentLengthHeader, content.length, new String(content, StandardCharsets.UTF_8)));
            myOutputStream.write(baos.toByteArray());
            myOutputStream.flush();


            //logger.info(getPrettyPrinter().writeValueAsString(request));
        } catch (IOException e) {
            if (requestedTermination) {
                // ignore this
            } else {
                logger.warn("Failed to send DAP request '" + request + "'", e);
            }
        }
        return true;
    }

    private void printMessageDebug(final @NotNull DAPProtocolMessage message) {
        Consumer<DAPEvent> consumer = eventHandlers.get(DAPEventNames.OUTPUT);
        if (consumer != null) consumer.accept(this.createNewDebugOutput(message));
    }

    private DAPEvent createNewDebugOutput(final @NotNull DAPProtocolMessage message) {
        DAPOutputEvent myDebugEvent = new DAPOutputEvent();
        myDebugEvent.body = new DAPOutputEvent.OutputEventBody();
        myDebugEvent.body.category = DAPOutputEvent.OutputEventBody.OutputCategory.TELEMETRY;


        if (message instanceof DAPEvent || message instanceof DAPResponse) {
            myDebugEvent.body.output = "---> ";
        } else {
            myDebugEvent.body.output = "<--- ";
        }

        try {
            myDebugEvent.body.output += myObjectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            myDebugEvent.body.output += message;
        }

        return myDebugEvent;
    }


    public Promise<List<Boolean>> whenPreviousEventsProcessed(int receive_sequence) {
        List<AsyncPromise<Boolean>> prevActiveEvents = new ArrayList<>();
        synchronized (myActiveEvents) {
            for (final Map.Entry<Integer, AsyncPromise<Boolean>> entry : myActiveEvents.entrySet()) {
                if(receive_sequence > entry.getKey()) prevActiveEvents.add(entry.getValue());
            }
        }

        return collectResults(prevActiveEvents);
    }

    protected void processEvent(DAPEvent event) {
        DAPEventNames eventNames = event.getEventId();

        if (eventNames != null) {
            Consumer<DAPEvent> consumer = eventHandlers.get(eventNames);
            if (consumer != null) {
                try {
                    consumer.accept(event);
                } catch (Throwable e) {
                    logger.error("Encountered exception while processing event "+event, e);
                }
            }
            else logger.info("Received unhandled "+event);
        }
        synchronized (myActiveEvents) {
            AsyncPromise<Boolean> prom = myActiveEvents.remove(event.sequence);
            prom.setResult(Boolean.TRUE);
        }
    }

    protected void processResponse(@NotNull DAPResponse response,
                                   @Async.Execute @NotNull Integer sequence) {
        AsyncPromise<DAPResponse> promise = openPromises.remove(sequence);
        if(promise != null) {
            if (response.success) promise.setResult(response);
            else if ("cancelled".equals(response.message)) promise.cancel();
            else {
                try {
                    logger.warn(new ObjectMapper().writeValueAsString(response));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                promise.setError(response.message);
            }
        } else {
            logger.info("Received unhandled "+response);
        }
    }

    protected void scheduleProcessing(DAPProtocolMessage message) {
        lastMessages.add(message);
        printMessageDebug(message);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if(message instanceof final DAPResponse response) {
                processResponse(response, response.request_sequence);
            } else if (message instanceof DAPEvent) {
                synchronized (myActiveEvents) {
                    myActiveEvents.put(message.sequence, new AsyncPromise<>());
                }

                processEvent((DAPEvent) message);
            } else {
                logger.info("Unknown DAP message: " + message);
                // TODO
            }
        });
    }
    public void setCancelRequest(final boolean cancelRequestEnabled) {
        this.myCancelRequestEnabled = cancelRequestEnabled;
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {

    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {

    }

    @Override
    public void onTextAvailable(@NotNull final ProcessEvent event, @NotNull final Key outputType) {
        String text = event.getText();
        lastMessages.add(text);

        List<DAPProtocolMessage> myNewMessages = myDAPParser.parse(text);

        for (final DAPProtocolMessage message : myNewMessages) {
            this.scheduleProcessing(message);
        }
    }
    public boolean wasTerminationRequested() {
        return requestedTermination;
    }

    public String getLastReceivedMessage() {
        Iterator it = this.lastMessages.iterator();
        for(int i=0; i<this.lastMessages.size()-1; i++) {
            it.next();
        }

        return it.hasNext() ? String.valueOf(it.next()) : "";
    }

    public void setTerminating() {
        this.requestedTermination = true;
    }
}
