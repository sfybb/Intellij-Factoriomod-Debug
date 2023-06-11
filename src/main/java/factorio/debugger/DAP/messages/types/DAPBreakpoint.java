package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPBreakpoint extends DAPAdditionalProperties {
    /**
     * The identifier for the breakpoint. It is needed if breakpoint events are
     * used to update or remove breakpoints.
     */
    @JsonProperty("id")
    public Integer id;

    /**
     * If true, the breakpoint could be set (but not necessarily at the desired
     * location).
     */
    @JsonProperty("verified")
    public boolean verified;

    /**
     * A message about the state of the breakpoint.
     * This is shown to the user and can be used to explain why a breakpoint could
     * not be verified.
     */
    @JsonProperty("message")
    public String message;

    /**
     * The source where the breakpoint is located.
     */
    @JsonProperty("source")
    public DAPSource source;

    /**
     * The start line of the actual range covered by the breakpoint.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * Start position of the source range covered by the breakpoint. It is
     * measured in UTF-16 code units and the client capability `columnsStartAt1`
     * determines whether it is 0- or 1-based.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the actual range covered by the breakpoint.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * End position of the source range covered by the breakpoint. It is measured
     * in UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     * If no end line is given, then the end column is assumed to be in the start
     * line.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;

    /**
     * A memory reference to where the breakpoint is set.
     */
    @JsonProperty("instructionReference")
    public String instructionReference;

    /**
     * The offset from the instruction reference.
     * This can be negative.
     */
    @JsonProperty("offset")
    public Integer offset;
}
