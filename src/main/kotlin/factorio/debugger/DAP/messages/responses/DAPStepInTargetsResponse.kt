package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPStepInTarget

@JsonTypeName("stepInTargets")
class DAPStepInTargetsResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: Body

    class Body : DAPAdditionalProperties() {
        /**
         * The possible step-in targets of the specified source location.
         */
        @JsonProperty("targets")
        lateinit var targets: Array<DAPStepInTarget>
    }
}
