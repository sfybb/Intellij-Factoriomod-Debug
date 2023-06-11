package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("exceptionInfo")
public class DAPExceptionInfoRequest extends DAPRequest<DAPExceptionInfoRequest.ExceptionInfoArguments> {
    public DAPExceptionInfoRequest(final int threadId) {
        this.arguments = new ExceptionInfoArguments(threadId);
    }

    public static class ExceptionInfoArguments {
        /**
         * Thread for which exception information should be retrieved.
         */
        @JsonProperty("threadId")
        public int threadId;

        public ExceptionInfoArguments(final int threadId) {
            this.threadId = threadId;
        }
    }
}
