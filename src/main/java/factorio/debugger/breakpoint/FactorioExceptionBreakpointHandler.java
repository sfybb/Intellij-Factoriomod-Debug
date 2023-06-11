package factorio.debugger.breakpoint;

import org.jetbrains.annotations.NotNull;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import factorio.debugger.FactorioDebugProcess;

public class FactorioExceptionBreakpointHandler extends XBreakpointHandler<XBreakpoint<FactorioExceptionBreakpointProperties>> {
    private final FactorioDebugProcess myDebugProcess;

    public FactorioExceptionBreakpointHandler(final FactorioDebugProcess debugProcess) {
        super(FactorioExceptionBreakpointType.class);
        this.myDebugProcess = debugProcess;
    }

    @Override
    public void registerBreakpoint(@NotNull final XBreakpoint<FactorioExceptionBreakpointProperties> breakpoint) {
        //this.myDebugProcess.addExceptionBreakpoint(breakpoint);
    }

    @Override
    public void unregisterBreakpoint(@NotNull final XBreakpoint<FactorioExceptionBreakpointProperties> breakpoint, final boolean temporary) {
        //this.myDebugProcess.addExceptionBreakpoint(breakpoint);
    }
}
