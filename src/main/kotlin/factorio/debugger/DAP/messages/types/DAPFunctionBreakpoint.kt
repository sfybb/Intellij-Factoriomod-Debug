package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPFunctionBreakpoint : DAPAdditionalProperties() {
    /**
     * The name of the function.
     */
    @JsonProperty("name")
    lateinit var name: String

    /**
     * An expression for conditional breakpoints.
     * It is only honored by a debug adapter if the corresponding capability
     * `supportsConditionalBreakpoints` is true.
     */
    @JsonProperty("condition")
    var condition: String? = null

    /**
     * An expression that controls how many hits of the breakpoint are ignored.
     * The debug adapter is expected to interpret the expression as needed.
     * The attribute is only honored by a debug adapter if the corresponding
     * capability `supportsHitConditionalBreakpoints` is true.
     */
    @JsonProperty("hitCondition")
    var hitCondition: String? = null
}
