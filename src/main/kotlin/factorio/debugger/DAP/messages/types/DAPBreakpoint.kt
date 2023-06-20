package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPBreakpoint : DAPAdditionalProperties() {
    /**
     * The identifier for the breakpoint. It is needed if breakpoint events are
     * used to update or remove breakpoints.
     */
    @JvmField
    @JsonProperty("id")
    var id: Int? = null

    /**
     * If true, the breakpoint could be set (but not necessarily at the desired
     * location).
     */
    @JvmField
    @JsonProperty("verified")
    var verified = false

    /**
     * A message about the state of the breakpoint.
     * This is shown to the user and can be used to explain why a breakpoint could
     * not be verified.
     */
    @JvmField
    @JsonProperty("message")
    var message: String? = null

    /**
     * The source where the breakpoint is located.
     */
    @JsonProperty("source")
    var source: DAPSource? = null

    /**
     * The start line of the actual range covered by the breakpoint.
     */
    @JsonProperty("line")
    var line: Int? = null

    /**
     * Start position of the source range covered by the breakpoint. It is
     * measured in UTF-16 code units and the client capability `columnsStartAt1`
     * determines whether it is 0- or 1-based.
     */
    @JsonProperty("column")
    var column: Int? = null

    /**
     * The end line of the actual range covered by the breakpoint.
     */
    @JsonProperty("endLine")
    var endLine: Int? = null

    /**
     * End position of the source range covered by the breakpoint. It is measured
     * in UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     * If no end line is given, then the end column is assumed to be in the start
     * line.
     */
    @JsonProperty("endColumn")
    var endColumn: Int? = null

    /**
     * A memory reference to where the breakpoint is set.
     */
    @JsonProperty("instructionReference")
    var instructionReference: String? = null

    /**
     * The offset from the instruction reference.
     * This can be negative.
     */
    @JsonProperty("offset")
    var offset: Int? = null
}
