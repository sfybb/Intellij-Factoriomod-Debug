package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("pause")
public class DAPPauseRequest extends DAPRequest<DAPPauseRequest.PauseArguments> {
    public DAPPauseRequest(int threadId) {
        this.arguments = new PauseArguments(threadId);
    }
    public static class PauseArguments extends DAPAdditionalProperties {
        public PauseArguments(int threadId) {
            this.threadId = threadId;
        }

        /**
         * Pause execution for this thread.
         */
        @JsonProperty("threadId")
        public int threadId;
    }
}
