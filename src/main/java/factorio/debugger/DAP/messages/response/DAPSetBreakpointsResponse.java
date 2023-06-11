package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPSource;
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint;

@JsonTypeName("setBreakpoints")
public class DAPSetBreakpointsResponse extends DAPResponse {
    @JsonProperty("body")
    public SetBreakpointsResponse body;
    public static class SetBreakpointsResponse extends DAPAdditionalProperties {
        /**
         * The source location of the breakpoints; either `source.path` or
         * `source.sourceReference` must be specified.
         */
        @JsonProperty("source")
        public DAPSource source;

        /**
         * The code locations of the breakpoints.
         */
        @JsonProperty("breakpoints")
        public DAPSourceBreakpoint[] breakpoints;

        /**
         * Deprecated: The code locations of the breakpoints.
         */
        @JsonProperty("lines")
        public Integer[] lines;

        /**
         * A value of true indicates that the underlying source has been modified
         * which results in new breakpoint locations.
         */
        @JsonProperty("sourceModified")
        public Boolean sourceModified;
    }

    @Override
    public String toString() {
        return super.toString() + ": added "+(body.breakpoints != null ? body.breakpoints.length : 0) + " breakpoints";
    }
}
