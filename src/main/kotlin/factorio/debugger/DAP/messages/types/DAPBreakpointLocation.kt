package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPBreakpointLocation extends DAPAdditionalProperties {
    /**
     * Start line of breakpoint location.
     */
    @JsonProperty("line")
    public int line;

    /**
     * The start position of a breakpoint location. Position is measured in UTF-16
     * code units and the client capability `columnsStartAt1` determines whether
     * it is 0- or 1-based.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of breakpoint location if the location covers a range.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * The end position of a breakpoint location (if the location covers a range).
     * Position is measured in UTF-16 code units and the client capability
     * `columnsStartAt1` determines whether it is 0- or 1-based.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;
}
