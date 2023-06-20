package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPDataBreakpoint : DAPAdditionalProperties() {
    /**
     * An id representing the data. This id is returned from the
     * `dataBreakpointInfo` request.
     */
    @JsonProperty("dataId")
    lateinit var dataId: String

    /**
     * The access type of the data.
     */
    @JsonProperty("accessType")
    var accessType: DAPDataBreakpointAccessType? = null

    /**
     * An expression for conditional breakpoints.
     */
    @JsonProperty("condition")
    var condition: String? = null

    /**
     * An expression that controls how many hits of the breakpoint are ignored.
     * The debug adapter is expected to interpret the expression as needed.
     */
    @JsonProperty("hitCondition")
    var hitCondition: String? = null
}
