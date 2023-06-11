package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPDataBreakpoint extends DAPAdditionalProperties {
    /**
     * An id representing the data. This id is returned from the
     * `dataBreakpointInfo` request.
     */
    @JsonProperty("dataId")
    public String dataId;

    /**
     * The access type of the data.
     */
    @JsonProperty("accessType")
    public DAPDataBreakpointAccessType accessType;

    /**
     * An expression for conditional breakpoints.
     */
    @JsonProperty("condition")
    public String condition;

    /**
     * An expression that controls how many hits of the breakpoint are ignored.
     * The debug adapter is expected to interpret the expression as needed.
     */
    @JsonProperty("hitCondition")
    public String hitCondition;
}
