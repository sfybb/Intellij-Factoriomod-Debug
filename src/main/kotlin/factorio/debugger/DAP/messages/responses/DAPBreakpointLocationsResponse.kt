package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPBreakpointLocation

@JsonTypeName("breakpointLocations")
class DAPBreakpointLocationsResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: BreakpointLocationsResponseBody

    class BreakpointLocationsResponseBody : DAPAdditionalProperties() {
        /**
         * Sorted set of possible breakpoint locations.
         */
        @JsonProperty("breakpoints")
        lateinit var breakpoints: Array<DAPBreakpointLocation>
    }

    override fun toString(): String {
        return "${super.toString()} possible locations ${body.breakpoints.joinToString(
            prefix = "[",
            separator = ", ",
            postfix = "]",
            limit = 10,
            truncated = "...",
            transform = { it.line.toString() }
        )}"
    }
}
