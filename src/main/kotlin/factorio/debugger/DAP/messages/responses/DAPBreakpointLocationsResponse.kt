package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPBreakpointLocation;

@JsonTypeName("breakpointLocations")
public class DAPBreakpointLocationsResponse extends DAPResponse {

    @JsonProperty("body")
    public BreakpointLocationsBody body;
    public static class BreakpointLocationsBody extends DAPAdditionalProperties {
        /**
         * Sorted set of possible breakpoint locations.
         */
        @JsonProperty("breakpoints")
        public DAPBreakpointLocation[] breakpoints;
    }
}
