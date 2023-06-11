package factorio.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.tang.intellij.lua.debugger.LuaDebuggerEvaluator;

public class LuaDebuggerSupportUtils {
    public static XDebuggerEvaluator getLuaEvaluator() throws NoClassDefFoundError {
        //Proxy.newProxyInstance(LuaDebuggerSupportUtils.getClass().getClassLoader(), XDebuggerEvaluator.class, );
        return new LuaDebuggerEvaluator() {
            @Override
            protected void eval(@NotNull final String s,
                                @NotNull final XDebuggerEvaluator.XEvaluationCallback xEvaluationCallback,
                                @Nullable final XSourcePosition xSourcePosition) {}
        };
    }
}
