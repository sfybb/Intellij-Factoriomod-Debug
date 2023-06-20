package factorio.debugger;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;
import com.intellij.DynamicBundle;

public class FactorioDebuggerBundle extends DynamicBundle {
    @NonNls public static final String BUNDLE = "messages.FactorioDebugger";
    private static final FactorioDebuggerBundle instance = new FactorioDebuggerBundle();

    private FactorioDebuggerBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return instance.getMessage(key, params);
    }

    @NotNull
    public static Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return instance.getLazyMessage(key, params);
    }
}
