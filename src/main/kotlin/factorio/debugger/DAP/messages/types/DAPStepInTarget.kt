package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPStepInTarget : DAPAdditionalProperties() {
    /**
     * Unique identifier for a step-in target.
     */
    @JvmField
    @JsonProperty("id")
    var id: Int = 0

    /**
     * The name of the step-in target (shown in the UI).
     */
    @JsonProperty("label")
    lateinit var label: String

    /**
     * The line of the step-in target.
     */
    @JvmField
    @JsonProperty("line")
    var line: Int? = null

    /**
     * Start position of the range covered by the step in target. It is measured
     * in UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     */
    @JvmField
    @JsonProperty("column")
    var column: Int? = null

    /**
     * The end line of the range covered by the step-in target.
     */
    @JvmField
    @JsonProperty("endLine")
    var endLine: Int? = null

    /**
     * End position of the range covered by the step in target. It is measured in
     * UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     */
    @JvmField
    @JsonProperty("endColumn")
    var endColumn: Int? = null
}
