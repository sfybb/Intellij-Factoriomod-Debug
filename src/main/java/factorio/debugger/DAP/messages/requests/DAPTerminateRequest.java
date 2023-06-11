package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("terminate")
public class DAPTerminateRequest extends DAPRequest<DAPTerminateRequest.TerminateArguments> {
    public DAPTerminateRequest() {
        arguments = new TerminateArguments();
    }
    public static class TerminateArguments extends DAPAdditionalProperties {
        /**
         * A value of true indicates that this `terminate` request is part of a
         * restart sequence.
         */
        @JsonProperty("restart")
        public boolean restart = false;
    }
}
