package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPBreakpointLocation : DAPAdditionalProperties() {
    /**
     * Start line of breakpoint location.
     */
    @JvmField
    @JsonProperty("line")
    var line = 0

    /**
     * The start position of a breakpoint location. Position is measured in UTF-16
     * code units and the client capability `columnsStartAt1` determines whether
     * it is 0- or 1-based.
     */
    @JsonProperty("column")
    var column: Int? = null

    /**
     * The end line of breakpoint location if the location covers a range.
     */
    @JsonProperty("endLine")
    var endLine: Int? = null

    /**
     * The end position of a breakpoint location (if the location covers a range).
     * Position is measured in UTF-16 code units and the client capability
     * `columnsStartAt1` determines whether it is 0- or 1-based.
     */
    @JsonProperty("endColumn")
    var endColumn: Int? = null
}
