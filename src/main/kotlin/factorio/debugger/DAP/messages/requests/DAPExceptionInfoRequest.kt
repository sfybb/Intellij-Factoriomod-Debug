package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPExceptionInfoRequest.ExceptionInfoArguments

@JsonTypeName("exceptionInfo")
class DAPExceptionInfoRequest(threadId: Int) : DAPRequest<ExceptionInfoArguments>(ExceptionInfoArguments(threadId)) {

    class ExceptionInfoArguments(
        /**
         * Thread for which exception information should be retrieved.
         */
        @field:JsonProperty("threadId") var threadId: Int
    ) : DAPAdditionalProperties()
}
