package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("attach")
public class DAPAttachRequest extends DAPRequest<DAPAttachRequest.AttachRequestArguments> {
    public static class AttachRequestArguments extends DAPAdditionalProperties {
        /**
         * Arbitrary data from the previous, restarted session.
         * The data is sent as the `restart` attribute of the `terminated` event.
         * The client should leave the data intact.
         */
        @JsonProperty("__restart")
        @JsonRawValue
        public String __restart;
    }
}
