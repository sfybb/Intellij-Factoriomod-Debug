package factorio.debugger.DAP

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.intellij.openapi.diagnostic.Logger
import factorio.debugger.DAP.messages.DAPProtocolMessage
import factorio.debugger.DAP.messages.events.DAPEvent
import factorio.debugger.DAP.messages.requests.DAPRequest
import factorio.debugger.DAP.messages.responses.DAPResponse

open class DAPMessageParser {
    private val logger = Logger.getInstance(DAPMessageParser::class.java)
    private var myLeftoverBuffer: String? = null
    private var headerComplete = false
    private var myContentLength = 0
    private val myObjectMapper: ObjectMapper = ObjectMapper()
    private var myDapMessages: ArrayList<DAPProtocolMessage> = ArrayList()

    protected val prettyPrinter: ObjectWriter
        get() = myObjectMapper.writerWithDefaultPrettyPrinter()
    protected val parsedMessages: List<DAPProtocolMessage>
        get() {
            if (myDapMessages.isEmpty()) return emptyList()
            val result: List<DAPProtocolMessage> = myDapMessages
            myDapMessages = ArrayList()
            return result
        }

    fun parse(text: String): List<DAPProtocolMessage> {
        val input = myLeftoverBuffer + text
        var offset = 0
        var lastOffset = 0
        while (offset >= 0) {
            lastOffset = offset
            offset = parseMessage(input, offset)
        }
        myLeftoverBuffer = if (lastOffset < input.length) {
            // we have left over characters
            input.substring(lastOffset)
        } else {
            ""
        }
        return parsedMessages
    }

    private fun parseMessage(input: String, offset: Int): Int {
        val messageStart = parseMessageHeader(input, offset)
        if (messageStart < 0) return -1
        return if (!headerComplete) {
            // error while parsing the header
            if (messageStart > offset) {
                // skip forward
                messageStart
            } else {
                // not enough characters to parse header
                // add header to leftovers
                -1
            }
        } else parseMessageContent(input, messageStart)

        // header parsed successfully
    }

    private fun parseMessageContent(input: String, messageStart: Int): Int {
        /*if (input.length() < messageStart + myContentLength) {
            // input doesn't contain the whole message
            return -1;
        }*/
        val messageEnd = input.length.coerceAtMost(messageStart + myContentLength)
        val myJsonMessage = input.substring(messageStart, messageEnd)
        try {
            val node = myObjectMapper.readTree(myJsonMessage)
            val message = when (node.at("/type").asText("")) {
                "request" -> myObjectMapper.treeToValue(node, DAPRequest::class.java)
                "response" -> myObjectMapper.treeToValue(node, DAPResponse::class.java)
                "event" -> myObjectMapper.treeToValue(node, DAPEvent::class.java)
                else -> null
            }
            if (message != null) {
                myDapMessages.add(message)
            } else {
                logger.info("Ignored message: ${prettyPrinter.writeValueAsString(node)}")
            }
        } catch (e: JsonProcessingException) {
            if (input.length < messageStart + myContentLength) {
                // input doesn't contain the whole message
                return -1
            }
            logger.warn("Received json: $myJsonMessage")
            logger.warn("Unable to parse message!", e)
        }
        return messageEnd
    }

    private fun parseMessageHeader(text: String, offset: Int): Int {
        val headerStart = text.indexOf(contentLengthHeader, offset)
        headerComplete = false
        if (headerStart < 0) return -1
        val endOfLine = text.indexOf("\r\n", headerStart)

        // not enough characters to fully parse the header yet
        if (endOfLine < 0 || text.length < endOfLine + 4) return -1
        val contentLengthStr = text.substring(headerStart + contentLengthHeader.length, endOfLine)
        if (text.regionMatches(endOfLine, "\r\n\r\n", 0, 4)) {
            try {
                val contentLength = contentLengthStr.toInt()

                //logger.warn("Expecting message of size: "+contentLength);
                if (contentLength < 10485760) { // 10 MB content length max
                    headerComplete = true
                    myContentLength = contentLength
                    return endOfLine + 4
                } else {
                    logger.warn("Content length too big! $contentLength")
                }
            } catch (e: NumberFormatException) {
                logger.warn("Invalid content length: '$contentLengthStr'")
            }
        } else {
            logger.warn("Invalid DAP header: No new lines int \"${text.substring(headerStart, endOfLine + 4)}\"")
        }
        return endOfLine
    }

    companion object {
        const val contentLengthHeader = "Content-Length: "
    }
}
