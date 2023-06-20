package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPExceptionFilterOptions extends DAPAdditionalProperties {
    /**
     * ID of an exception filter returned by the `exceptionBreakpointFilters`
     * capability.
     */
    @JsonProperty("filterId")
    public String filterId;

    /**
     * An expression for conditional exceptions.
     * The exception breaks into the debugger if the result of the condition is
     * true.
     */
    @JsonProperty("condition")
    public String condition;
}
