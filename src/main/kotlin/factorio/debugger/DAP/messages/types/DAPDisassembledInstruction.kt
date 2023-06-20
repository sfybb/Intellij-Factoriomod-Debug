package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPDisassembledInstruction : DAPAdditionalProperties() {
    /**
     * The address of the instruction. Treated as a hex value if prefixed with
     * `0x`, or as a decimal value otherwise.
     */
    @JsonProperty("address")
    lateinit var address: String

    /**
     * Raw bytes representing the instruction and its operands, in an
     * implementation-defined format.
     */
    @JsonProperty("instructionBytes")
    var instructionBytes: String? = null

    /**
     * Text representing the instruction and its operands, in an
     * implementation-defined format.
     */
    @JsonProperty("instruction")
    lateinit var instruction: String

    /**
     * Name of the symbol that corresponds with the location of this instruction,
     * if any.
     */
    @JsonProperty("symbol")
    var symbol: String? = null

    /**
     * Source location that corresponds to this instruction, if any.
     * Should always be set (if available) on the first instruction returned,
     * but can be omitted afterwards if this instruction maps to the same source
     * file as the previous instruction.
     */
    @JsonProperty("location")
    var location: DAPSource? = null

    /**
     * The line within the source location that corresponds to this instruction,
     * if any.
     */
    @JsonProperty("line")
    var line: Int? = null

    /**
     * The column within the line that corresponds to this instruction, if any.
     */
    @JsonProperty("column")
    var column: Int? = null

    /**
     * The end line of the range that corresponds to this instruction, if any.
     */
    @JsonProperty("endLine")
    var endLine: Int? = null

    /**
     * The end column of the range that corresponds to this instruction, if any.
     */
    @JsonProperty("endColumn")
    var endColumn: Int? = null
}
