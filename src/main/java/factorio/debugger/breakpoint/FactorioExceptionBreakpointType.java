package factorio.debugger.breakpoint;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;

public class FactorioExceptionBreakpointType extends XBreakpointType<XBreakpoint<FactorioExceptionBreakpointProperties>, FactorioExceptionBreakpointProperties>  {
    protected FactorioExceptionBreakpointType() {
        super("factorio-exception", "Exception");
    }

    @Override
    public @Nls String getDisplayText(final XBreakpoint<FactorioExceptionBreakpointProperties> breakpoint) {
        return null;
    }

    @Override
    public @NotNull Icon getEnabledIcon() {
        return AllIcons.Debugger.Db_exception_breakpoint;
    }

    @Override
    public @NotNull Icon getDisabledIcon() {
        return AllIcons.Debugger.Db_disabled_exception_breakpoint;
    }

    @Override
    public @Nullable FactorioExceptionBreakpointProperties createProperties() {
        return new FactorioExceptionBreakpointProperties();
    }
}
