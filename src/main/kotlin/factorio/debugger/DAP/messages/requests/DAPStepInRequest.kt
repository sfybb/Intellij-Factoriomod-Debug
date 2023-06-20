package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPStepInRequest.StepInArguments
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity

@JsonTypeName("stepIn")
class DAPStepInRequest : DAPRequest<StepInArguments> {
    constructor(threadId: Int) : super(StepInArguments(threadId))

    constructor()  : super(StepInArguments())

    class StepInArguments : DAPAdditionalProperties {
        /**
         * Specifies the thread for which to resume execution for one step-into (of
         * the given granularity).
         */
        @JsonProperty("threadId")
        var threadId: Int = 0

        /**
         * If this flag is true, all other suspended threads are not resumed.
         */
        @JsonProperty("singleThread")
        var singleThread: Boolean?

        /**
         * Id of the target to step into.
         */
        @JvmField
        @JsonProperty("targetId")
        var targetId: Int? = null

        /**
         * Stepping granularity. If no granularity is specified, a granularity of
         * `statement` is assumed.
         */
        @JsonProperty("granularity")
        var granularity: DAPSteppingGranularity? = null

        constructor(threadId: Int) {
            this.threadId = threadId
            singleThread = true
        }

        constructor() {
            singleThread = false
            targetId = 0
        }
    }
}
