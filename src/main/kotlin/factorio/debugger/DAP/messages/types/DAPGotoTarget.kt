package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPGotoTarget : DAPAdditionalProperties() {
    /**
     * Unique identifier for a goto target. This is used in the `goto` request.
     */
    @JsonProperty("id")
    var id = 0

    /**
     * The name of the goto target (shown in the UI).
     */
    @JsonProperty("label")
    lateinit var label: String

    /**
     * The line of the goto target.
     */
    @JsonProperty("line")
    var line: Int = 0

    /**
     * The column of the goto target.
     */
    @JsonProperty("column")
    var column: Int? = null

    /**
     * The end line of the range covered by the goto target.
     */
    @JsonProperty("endLine")
    var endLine: Int? = null

    /**
     * The end column of the range covered by the goto target.
     */
    @JsonProperty("endColumn")
    var endColumn: Int? = null

    /**
     * A memory reference for the instruction pointer value represented by this
     * target.
     */
    @JsonProperty("instructionPointerReference")
    var instructionPointerReference: String? = null
}
