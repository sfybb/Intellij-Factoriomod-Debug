package factorio.debugger.game;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FactorioRuntimeEnvironmentType<T extends FactorioRuntimeEnvironment> {

    public abstract @NotNull String getName();

    public abstract String getID();

    public boolean isAvailable() {
        return true;
    }

    public abstract T create(@NotNull String refPath);

    public abstract @Nullable T findByReferenceName(@NotNull String refName);

    public abstract @NotNull List<T> getEnvironments();
    public abstract void setEnvironments(@NotNull List<? extends FactorioRuntimeEnvironment> envs);

    public abstract List<FactorioRuntimeEnvironmentRef> getEnvironmentRefs();

    public abstract T showAddDialog();

    public abstract T edit(final FactorioRuntimeEnvironment runtimeEnv);

    public abstract String getChooserDialogTitle();

    public abstract String getEnvironmentName();
}
