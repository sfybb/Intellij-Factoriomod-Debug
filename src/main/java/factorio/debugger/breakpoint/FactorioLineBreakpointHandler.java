package factorio.debugger.breakpoint;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import factorio.debugger.FactorioDebugProcess;

public class FactorioLineBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<XBreakpointProperties>> {
    private Logger logger = Logger.getInstance(FactorioLineBreakpointHandler.class);
    protected final FactorioDebugProcess myDebugProcess;
    protected final Map<XLineBreakpoint<XBreakpointProperties>, XSourcePosition> myBreakPointPositions;

    public FactorioLineBreakpointHandler(@NotNull final FactorioDebugProcess factorioDebugProcess, Class breakpointTypeClass) {
        super(breakpointTypeClass);
        myDebugProcess = factorioDebugProcess;
        myBreakPointPositions = Maps.newHashMap();
    }

    public void reregisterBreakpoints() {
        for (final XLineBreakpoint<XBreakpointProperties> breakpoint : myBreakPointPositions.keySet()) {
            this.unregisterBreakpoint(breakpoint, false);
            this.registerBreakpoint(breakpoint);
        }

    }

    @Override
    public void registerBreakpoint(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint) {
        XSourcePosition position = breakpoint.getSourcePosition();
        if (position != null && position.getFile().isValid()) {
            this.myDebugProcess.addBreakpoint(position, breakpoint);
            this.myBreakPointPositions.put(breakpoint, position);
        }
    }

    @Override
    public void unregisterBreakpoint(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint, final boolean temporary) {
        XSourcePosition position = this.myBreakPointPositions.get(breakpoint);
        if (position != null && position.getFile().isValid()) {
            this.myDebugProcess.removeBreakpoint(position, breakpoint);
            this.myBreakPointPositions.remove(breakpoint, position);
        }
    }
}
