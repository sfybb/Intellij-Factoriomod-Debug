package factorio.debugger.frames;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import factorio.debugger.DAP.messages.types.DAPVariable;

public interface FactorioVariableContainer {

    String getName();

    boolean modify(@NotNull String variableName, DAPVariable newValue);

    boolean hasChild(@NotNull String variableName);

    @Nullable FactorioVariableContainer getParent();
    int getReferenceId();
}
