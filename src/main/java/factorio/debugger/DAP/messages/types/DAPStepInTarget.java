package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPStepInTarget extends DAPAdditionalProperties {
    /**
     * Unique identifier for a step-in target.
     */
    @JsonProperty("id")
    public Integer id;

    /**
     * The name of the step-in target (shown in the UI).
     */
    @JsonProperty("label")
    public String label;

    /**
     * The line of the step-in target.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * Start position of the range covered by the step in target. It is measured
     * in UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the range covered by the step-in target.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * End position of the range covered by the step in target. It is measured in
     * UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;
}
