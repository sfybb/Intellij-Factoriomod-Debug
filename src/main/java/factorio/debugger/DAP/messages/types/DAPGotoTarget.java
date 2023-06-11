package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPGotoTarget extends DAPAdditionalProperties {
    /**
     * Unique identifier for a goto target. This is used in the `goto` request.
     */
    @JsonProperty("id")
    public int id;

    /**
     * The name of the goto target (shown in the UI).
     */
    @JsonProperty("label")
    public String label;

    /**
     * The line of the goto target.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * The column of the goto target.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the range covered by the goto target.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * The end column of the range covered by the goto target.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;

    /**
     * A memory reference for the instruction pointer value represented by this
     * target.
     */
    @JsonProperty("instructionPointerReference")
    public String instructionPointerReference;
}
