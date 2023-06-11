package factorio.debugger.game;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;

public class FactorioGameRuntimeEnvironment implements FactorioRuntimeEnvironment {
    static final String WIN_EXE_EXT = ".exe";
    private final @Nullable FactorioVersion myVersion;
    private final String myHomePath;

    public FactorioGameRuntimeEnvironment(@NotNull String homePath) {
        String newHomePath = FactorioUtil.getHomePathFromExec(homePath);
        if(newHomePath != null) homePath = newHomePath;

        this.myHomePath = FileUtil.toSystemIndependentName(homePath);
        this.myVersion = FactorioUtil.getVersion(myHomePath);
    }

    @Override
    public @NotNull FactorioRuntimeEnvironmentType<? extends FactorioRuntimeEnvironment> getType() {
        return FactorioGameRuntimeEnvironmentType.getInstance();
    }

    @Override
    public @NotNull String getReferenceName() {
        return myHomePath;
    }

    @Override
    public @NotNull String getPresentableName() {
        return getPresentablePath(myHomePath);
    }

    @Override
    public @Nullable FactorioVersion getVersion() {
        return myVersion;
    }

    @Override
    public @NotNull String getSystemIndependentPath() {
        return myHomePath;
    }

    @Override
    public boolean isValid() {
        return myVersion != null && FactorioUtil.checkForFactorio(myHomePath);
    }

    public String getExecuteablePath() {
        String execPath = Path.of(myHomePath, "bin", "x64", "factorio").toString();
        return SystemInfo.isWindows ? execPath + WIN_EXE_EXT : execPath;
    }

    @Override
    public FactorioRuntimeEnvironmentRef toRef() {
        return new FactorioRuntimeEnvironmentRef(this.myHomePath);
    }

    public String getBasePath() {
        return myHomePath;
    }
}
