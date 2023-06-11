package factorio.debugger.DAP.messages;


import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DAPProtocolMessage extends DAPAdditionalProperties {
    /**
     * Message type.
     * Values: 'request', 'response', 'event', etc.
     */
    @JsonProperty("type")
    public String type;

    /**
     * Sequence number of the message (also known as message ID). The `seq` for
     * the first message sent by a client or debug adapter is 1, and for each
     * subsequent message is 1 greater than the previous message sent by that
     * actor. `seq` can be used to order requests, responses, and events, and to
     * associate requests with their corresponding responses. For protocol
     * messages of type `request` the sequence number can be used to cancel the
     * request.
     */
    @JsonProperty("seq")
    public int sequence;
}
