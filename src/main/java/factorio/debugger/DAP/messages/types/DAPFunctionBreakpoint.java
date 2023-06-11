package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPFunctionBreakpoint extends DAPAdditionalProperties {
    /**
     * The name of the function.
     */
    @JsonProperty("name")
    public String name;

    /**
     * An expression for conditional breakpoints.
     * It is only honored by a debug adapter if the corresponding capability
     * `supportsConditionalBreakpoints` is true.
     */
    @JsonProperty("condition")
    public String condition;

    /**
     * An expression that controls how many hits of the breakpoint are ignored.
     * The debug adapter is expected to interpret the expression as needed.
     * The attribute is only honored by a debug adapter if the corresponding
     * capability `supportsHitConditionalBreakpoints` is true.
     */
    @JsonProperty("hitCondition")
    public String hitCondition;
}
