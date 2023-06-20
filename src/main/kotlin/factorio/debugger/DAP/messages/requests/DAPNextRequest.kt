package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPNextRequest.NextArguments
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity

@JsonTypeName("next")
class DAPNextRequest : DAPRequest<NextArguments> {
    constructor(threadId: Int, granularity: DAPSteppingGranularity?)
            : super(NextArguments(threadId, granularity)) {
        arguments = NextArguments(threadId, granularity)
    }

    constructor(threadId: Int)
            : super(NextArguments(threadId, DAPSteppingGranularity.STATEMENT))

    constructor(granularity: DAPSteppingGranularity?)
            : super(NextArguments(granularity))

    constructor()
            : super(NextArguments(DAPSteppingGranularity.STATEMENT))

    class NextArguments : DAPAdditionalProperties {
        constructor(threadId: Int, granularity: DAPSteppingGranularity?) {
            this.threadId = threadId
            this.singleThread = true
            this.granularity = granularity
        }

        constructor(granularity: DAPSteppingGranularity?) {
            this.threadId = 0
            this.singleThread = false
            this.granularity = granularity
        }

        /**
         * Specifies the thread for which to resume execution for one step (of the
         * given granularity).
         */
        @JsonProperty("threadId")
        var threadId: Int

        /**
         * If this flag is true, all other suspended threads are not resumed.
         */
        @JsonProperty("singleThread")
        var singleThread: Boolean?

        /**
         * Stepping granularity. If no granularity is specified, a granularity of
         * `statement` is assumed.
         */
        @JsonProperty("granularity")
        var granularity: DAPSteppingGranularity?
    }
}
