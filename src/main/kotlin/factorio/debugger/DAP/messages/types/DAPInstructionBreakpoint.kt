package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPInstructionBreakpoint : DAPAdditionalProperties() {
    /**
     * The instruction reference of the breakpoint.
     * This should be a memory or instruction pointer reference from an
     * `EvaluateResponse`, `Variable`, `StackFrame`, `GotoTarget`, or
     * `Breakpoint`.
     */
    @JsonProperty("instructionReference")
    lateinit var instructionReference: String

    /**
     * The offset from the instruction reference.
     * This can be negative.
     */
    @JsonProperty("offset")
    var offset: Int? = null

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
