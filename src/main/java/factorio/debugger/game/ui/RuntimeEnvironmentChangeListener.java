package factorio.debugger.game.ui;

import org.jetbrains.annotations.NotNull;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;

public interface RuntimeEnvironmentChangeListener {
    void runtimeEnvironmentChanged(@NotNull FactorioRuntimeEnvironmentRef selectedItem);
}
