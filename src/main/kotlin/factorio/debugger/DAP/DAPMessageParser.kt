package factorio.debugger.DAP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intellij.openapi.diagnostic.Logger;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.DAPProtocolMessage;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.DAPResponse;

public class DAPMessageParser {
    private final Logger logger = Logger.getInstance(DAPMessageParser.class);
    public static final String contentLengthHeader = "Content-Length: ";

    private String myLeftoverBuffer;
    private boolean headerComplete;
    private int myContentLength;
    private final ObjectMapper myObjectMapper;

    private @NotNull ArrayList<DAPProtocolMessage> myDapMessages;

    public DAPMessageParser() {
        myObjectMapper = new ObjectMapper();
        myDapMessages = new ArrayList<>();
    }

    protected ObjectWriter getPrettyPrinter() {
        return myObjectMapper.writerWithDefaultPrettyPrinter();
    }

    protected List<DAPProtocolMessage> getParsedMessages() {
        if(myDapMessages.isEmpty()) return Collections.emptyList();

        List<DAPProtocolMessage> result = myDapMessages;
        myDapMessages = new ArrayList<>();
        return result;
    }

    public List<DAPProtocolMessage> parse(@NotNull final String text) {
        String input = myLeftoverBuffer + text;

        int offset = 0, lastOffset = 0;
        while(offset >= 0) {
            lastOffset = offset;
            offset = parseMessage(input, offset);
        }

        if (lastOffset < input.length()) {
            // we have left over characters
            myLeftoverBuffer = input.substring(lastOffset);
        } else {
            myLeftoverBuffer = "";
        }

        return getParsedMessages();
    }

    private int parseMessage(final String input, final int offset) {
        int messageStart = parseMessageHeader(input, offset);
        if (messageStart < 0) return -1;

        if (!headerComplete) {
            // error while parsing the header
            if (messageStart > offset) {
                // skip forward
                return messageStart;
            } else {
                // not enough characters to parse header
                // add header to leftovers
                return -1;
            }
        }

        // header parsed successfully
        return parseMessageContent(input, messageStart);
    }

    private int parseMessageContent(final String input, final int messageStart) {
        /*if (input.length() < messageStart + myContentLength) {
            // input doesn't contain the whole message
            return -1;
        }*/

        int messageEnd = Math.min(input.length(), messageStart + myContentLength);

        String myJsonMessage = input.substring(messageStart, messageEnd);

        try {
            JsonNode node = myObjectMapper.readTree(myJsonMessage);

            String messageType = node.at("/type").asText("");

            DAPProtocolMessage message = switch (messageType) {
                case "request" -> myObjectMapper.treeToValue(node, DAPRequest.class);
                case "response" -> myObjectMapper.treeToValue(node, DAPResponse.class);
                case "event" -> myObjectMapper.treeToValue(node, DAPEvent.class);
                default -> null;
            };

            if (message != null) {
                myDapMessages.add(message);
            } else {
                logger.info(String.format("Ignored message: %s", getPrettyPrinter().writeValueAsString(node)));
            }
        } catch (JsonProcessingException e) {
            if (input.length() < messageStart + myContentLength) {
                // input doesn't contain the whole message
                return -1;
            }

            logger.warn("Received json: "+myJsonMessage);
            logger.warn("Unable to parse message!", e);
        }

        return messageEnd;
    }

    private int parseMessageHeader(@NotNull final String text, int offset) {
        int headerStart = text.indexOf(contentLengthHeader, offset);
        headerComplete = false;
        if (headerStart < 0) return -1;

        int endOfLine = text.indexOf("\r\n", headerStart);

        // not enough characters to fully parse the header yet
        if(endOfLine < 0 || text.length() < endOfLine + 4) return -1;

        String contentLengthStr = text.substring(headerStart + contentLengthHeader.length(), endOfLine);
        if(text.regionMatches(endOfLine, "\r\n\r\n", 0, 4)) {
            try {
                int contentLength = Integer.parseInt(contentLengthStr);

                //logger.warn("Expecting message of size: "+contentLength);
                if (contentLength < 10485760) { // 10 MB content length max
                    headerComplete = true;
                    myContentLength = contentLength;
                    return endOfLine+4;
                } else {
                    logger.warn("Content length too big! "+contentLength);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid content length: '"+contentLengthStr+"'");
            }
        } else {
            logger.warn(String.format("Invalid DAP header: No new lines int \"%s\"", text.substring(headerStart, endOfLine+4)));
        }
        return endOfLine;
    }
}
