package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("continue")
public class DAPContinueRequest extends DAPRequest<DAPContinueRequest.ContinueArguments> {
    public DAPContinueRequest(int threadId) {
        this.arguments = new ContinueArguments(threadId);
    }

    public DAPContinueRequest() {
        this.arguments = new ContinueArguments();
    }
    public static class ContinueArguments extends DAPAdditionalProperties {
        public ContinueArguments() {
            this.threadId = 0;
            this.singleThread = false;
        }

        public ContinueArguments(int threadId) {
            this.threadId = threadId;
            this.singleThread = true;
        }

        /**
         * Specifies the active thread. If the debug adapter supports single thread
         * execution (see `supportsSingleThreadExecutionRequests`) and the argument
         * `singleThread` is true, only the thread with this ID is resumed.
         */
        @JsonProperty("threadId")
        public int threadId;

        /**
         * If this flag is true, execution is resumed only for the thread with given
         * `threadId`.
         */
        @JsonProperty("singleThread")
        public Boolean singleThread;
    }
}
