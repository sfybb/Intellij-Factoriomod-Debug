package factorio.debugger.DAP.messages.requests;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPSource;
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint;

@JsonTypeName("setBreakpoints")
public class DAPSetBreakpointsRequest extends DAPRequest<DAPSetBreakpointsRequest.SetBreakpointsArguments> {

    public DAPSetBreakpointsRequest() {
        arguments = new SetBreakpointsArguments();
    }
    public DAPSetBreakpointsRequest(@NotNull final VirtualFile file, final List<Integer> breakpointLines) {
        arguments =  new SetBreakpointsArguments(file, breakpointLines);
    }

    public DAPSetBreakpointsRequest(@NotNull final XSourcePosition breakpointPos) {
        arguments =  new SetBreakpointsArguments(breakpointPos.getFile(), breakpointPos.getLine());
    }
    public static class SetBreakpointsArguments extends DAPAdditionalProperties {
        public SetBreakpointsArguments(@NotNull final VirtualFile file, final List<Integer> breakpointLines) {
            this.source = new DAPSource(file);
            this.breakpoints = new DAPSourceBreakpoint[breakpointLines.size()];
            this.lines = new int[breakpointLines.size()];
            for (int i = 0; i < breakpointLines.size(); i++) {
                this.breakpoints[i] = new DAPSourceBreakpoint(breakpointLines.get(i));
                this.lines[i] = breakpointLines.get(i);
            }
        }

        public SetBreakpointsArguments(@NotNull final VirtualFile file, final int breakpointLine) {
            this.source = new DAPSource(file);
            this.breakpoints = new DAPSourceBreakpoint[1];
            this.breakpoints[0] = new DAPSourceBreakpoint(breakpointLine);
            this.lines = new int[1];
            this.lines[0] = breakpointLine;
        }

        public SetBreakpointsArguments() {

        }

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
        public int[] lines;

        /**
         * A value of true indicates that the underlying source has been modified
         * which results in new breakpoint locations.
         */
        @JsonProperty("sourceModified")
        public boolean sourceModified;
    }
}
