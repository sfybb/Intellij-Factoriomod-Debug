package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("scopes")
public class DAPScopesRequest extends DAPRequest<DAPScopesRequest.ScopesArguments> {
    public DAPScopesRequest(int frameId) {
        this.arguments = new ScopesArguments(frameId);
    }
    public static class ScopesArguments extends DAPAdditionalProperties {
        public ScopesArguments(int frameId) {
            this.frameId = frameId;
        }
        /**
         * Retrieve the scopes for the stack frame identified by `frameId`. The
         * `frameId` must have been obtained in the current suspended state. See
         * 'Lifetime of Object References' in the Overview section for details.
         */
        @JsonProperty("frameId")
        public int frameId;
    }
}
