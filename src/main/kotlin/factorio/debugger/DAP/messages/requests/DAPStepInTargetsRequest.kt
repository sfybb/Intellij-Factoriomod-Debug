package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.requests.DAPStepInTargetsRequest.StepInTargetsRequestArguments

@JsonTypeName("stepInTargets")
class DAPStepInTargetsRequest(frameId: Int)
    : DAPRequest<StepInTargetsRequestArguments>(StepInTargetsRequestArguments(frameId)) {

    class StepInTargetsRequestArguments(
        /**
         * The stack frame for which to retrieve the possible step-in targets.
         */
        @field:JsonProperty("frameId") var frameId: Int
    )
}
