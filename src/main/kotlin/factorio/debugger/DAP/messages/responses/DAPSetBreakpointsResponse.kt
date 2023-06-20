package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPSource
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint

@JsonTypeName("setBreakpoints")
class DAPSetBreakpointsResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: SetBreakpointsResponseBody

    class SetBreakpointsResponseBody : DAPAdditionalProperties() {
        /**
         * The source location of the breakpoints; either `source.path` or
         * `source.sourceReference` must be specified.
         */
        @JsonProperty("source")
        var source: DAPSource? = null

        /**
         * The code locations of the breakpoints.
         */
        @JsonProperty("breakpoints")
        lateinit var breakpoints: Array<DAPSourceBreakpoint>

        /**
         * Deprecated: The code locations of the breakpoints.
         */
        @JsonProperty("lines")
        var lines: Array<Int>? = null

        /**
         * A value of true indicates that the underlying source has been modified
         * which results in new breakpoint locations.
         */
        @JsonProperty("sourceModified")
        var sourceModified: Boolean? = null
    }

    override fun toString(): String {
        return "${super.toString()}: added ${body.breakpoints.size} breakpoints"
    }
}
