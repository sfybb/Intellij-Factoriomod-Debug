package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPDisassembledInstruction extends DAPAdditionalProperties {
    /**
     * The address of the instruction. Treated as a hex value if prefixed with
     * `0x`, or as a decimal value otherwise.
     */
    @JsonProperty("address")
    public String address;

    /**
     * Raw bytes representing the instruction and its operands, in an
     * implementation-defined format.
     */
    @JsonProperty("instructionBytes")
    public String instructionBytes;

    /**
     * Text representing the instruction and its operands, in an
     * implementation-defined format.
     */
    @JsonProperty("instruction")
    public String instruction;

    /**
     * Name of the symbol that corresponds with the location of this instruction,
     * if any.
     */
    @JsonProperty("symbol")
    public String symbol;

    /**
     * Source location that corresponds to this instruction, if any.
     * Should always be set (if available) on the first instruction returned,
     * but can be omitted afterwards if this instruction maps to the same source
     * file as the previous instruction.
     */
    @JsonProperty("location")
    public DAPSource location;

    /**
     * The line within the source location that corresponds to this instruction,
     * if any.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * The column within the line that corresponds to this instruction, if any.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the range that corresponds to this instruction, if any.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * The end column of the range that corresponds to this instruction, if any.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;
}
