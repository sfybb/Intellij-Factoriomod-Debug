package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPContinueRequest.ContinueArguments

@JsonTypeName("continue")
class DAPContinueRequest : DAPRequest<ContinueArguments> {
    constructor(threadId: Int) : super(ContinueArguments(threadId))

    constructor() : super(ContinueArguments())

    class ContinueArguments : DAPAdditionalProperties {
        constructor() {
            this.threadId = 0
            this.singleThread = false
        }

        constructor(threadId: Int) {
            this.threadId = threadId
            this.singleThread = true
        }

        /**
         * Specifies the active thread. If the debug adapter supports single thread
         * execution (see `supportsSingleThreadExecutionRequests`) and the argument
         * `singleThread` is true, only the thread with this ID is resumed.
         */
        @JsonProperty("threadId")
        var threadId: Int

        /**
         * If this flag is true, execution is resumed only for the thread with given
         * `threadId`.
         */
        @JsonProperty("singleThread")
        var singleThread: Boolean?
    }
}
