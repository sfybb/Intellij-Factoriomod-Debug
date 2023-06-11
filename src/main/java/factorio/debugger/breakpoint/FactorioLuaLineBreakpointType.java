package factorio.debugger.breakpoint;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import factorio.debugger.FactorioDebuggerEditorsProvider;

public class FactorioLuaLineBreakpointType extends XLineBreakpointTypeBase {
    public static final String ID = "lua-line"; // javascript-exception
    protected FactorioLuaLineBreakpointType(@NonNls @NotNull final String id, @Nls @NotNull final String title, @Nullable final XDebuggerEditorsProvider editorsProvider) {
        super(id, title, editorsProvider);
    }

    public FactorioLuaLineBreakpointType() {
        super(ID, "Factorio Lua Line Breakpoint", new FactorioDebuggerEditorsProvider());
    }
}
