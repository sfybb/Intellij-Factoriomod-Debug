package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPPauseRequest.PauseArguments

@JsonTypeName("pause")
class DAPPauseRequest(threadId: Int) : DAPRequest<PauseArguments>(PauseArguments(threadId)) {

    class PauseArguments(
        /**
         * Pause execution for this thread.
         */
        @field:JsonProperty("threadId") var threadId: Int
    ) : DAPAdditionalProperties()
}
