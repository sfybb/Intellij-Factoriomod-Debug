package factorio.debugger.breakpoint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;

public class FactorioExceptionBreakpointProperties extends XBreakpointProperties<FactorioExceptionBreakpointProperties> {


    @Override
    public @Nullable FactorioExceptionBreakpointProperties getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull final FactorioExceptionBreakpointProperties state) {

    }

    @Override
    public void noStateLoaded() {
        super.noStateLoaded();
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
    }
}
