package factorio.debugger.game;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.io.FileUtil;

public class FactorioRuntimeEnvironmentRef {
    private final String refPath;
    public FactorioRuntimeEnvironmentRef(@NotNull String ref) {
        refPath = FileUtil.toSystemIndependentName(ref);
    }

    public static FactorioRuntimeEnvironmentRef create(@NotNull final String s) {
        return new FactorioRuntimeEnvironmentRef(s);
    }

    public @NotNull @NonNls String getReferenceName() {
        return refPath;
    }

    public @Nullable FactorioFMTKRuntimeEnvironment resolveAsFMTK() {
        FactorioFMTKRuntimeEnvironment fmtkResolveResult = FactorioFMTKRuntimeEnvironmentType.getInstance().findByReferenceName(refPath);
        if(fmtkResolveResult == null) fmtkResolveResult = new FactorioFMTKRuntimeEnvironment(refPath);
        return fmtkResolveResult.isValid() ? fmtkResolveResult : null;
    }

    public @Nullable FactorioGameRuntimeEnvironment resolveAsFactorioGame() {
        FactorioGameRuntimeEnvironment gameResolveResult = FactorioGameRuntimeEnvironmentType.getInstance().findByReferenceName(refPath);
        if(gameResolveResult == null) gameResolveResult = new FactorioGameRuntimeEnvironment(refPath);
        return gameResolveResult.isValid() ? gameResolveResult : null;
    }

    public @Nullable FactorioRuntimeEnvironment resolve() {
        FactorioRuntimeEnvironment runtimeEnv = resolveAsFMTK();
        return runtimeEnv == null ? resolveAsFactorioGame() : runtimeEnv;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final FactorioRuntimeEnvironmentRef that = (FactorioRuntimeEnvironmentRef) o;

        return refPath.equals(that.refPath);
    }

    @Override
    public int hashCode() {
        return refPath.hashCode();
    }
}
