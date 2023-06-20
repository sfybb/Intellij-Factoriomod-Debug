package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPSourceBreakpoint extends DAPAdditionalProperties {
    /**
     * The source line of the breakpoint or logpoint.
     */
    @JsonProperty("line")
    public int line;

    /**
     * Start position within source line of the breakpoint or logpoint. It is
     * measured in UTF-16 code units and the client capability `columnsStartAt1`
     * determines whether it is 0- or 1-based.
     */
    @JsonProperty("column")
    public int column;

    /**
     * The expression for conditional breakpoints.
     * It is only honored by a debug adapter if the corresponding capability
     * `supportsConditionalBreakpoints` is true.
     */
    @JsonProperty("condition")
    public String condition;

    /**
     * The expression that controls how many hits of the breakpoint are ignored.
     * The debug adapter is expected to interpret the expression as needed.
     * The attribute is only honored by a debug adapter if the corresponding
     * capability `supportsHitConditionalBreakpoints` is true.
     * If both this property and `condition` are specified, `hitCondition` should
     * be evaluated only if the `condition` is met, and the debug adapter should
     * stop only if both conditions are met.
     */
    @JsonProperty("hitCondition")
    public String hitCondition;

    /**
     * If this attribute exists and is non-empty, the debug adapter must not
     * 'break' (stop)
     * but log the message instead. Expressions within `{}` are interpolated.
     * The attribute is only honored by a debug adapter if the corresponding
     * capability `supportsLogPoints` is true.
     * If either `hitCondition` or `condition` is specified, then the message
     * should only be logged if those conditions are met.
     */
    @JsonProperty("logMessage")
    public String logMessage;

    public DAPSourceBreakpoint() {

    }

    public DAPSourceBreakpoint(final int line) {
        this.line = line;
        this.column = 0;
    }
}
