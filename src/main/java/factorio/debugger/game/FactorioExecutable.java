package factorio.debugger.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;

public class FactorioExecutable {

    private final @Nullable FactorioVersion myVersion;
    private final String myHomePath;

    public FactorioExecutable(@NotNull String homePath) {
        this.myHomePath = FileUtil.toSystemIndependentName(homePath);
        this.myVersion = FactorioUtil.getVersion(this.myHomePath);
    }

    public FactorioExecutable(@NotNull String homePath, @NotNull FactorioVersion version) {
        this.myHomePath = FileUtil.toSystemIndependentName(homePath);
        this.myVersion = FactorioVersion.parse(this.myHomePath);
    }

    public @NotNull String getReferenceName() {
        String path = this.myHomePath;
        if (SystemInfo.isWindows && path.endsWith(".exe")) {
            path = path.substring(0, path.length() - ".exe".length());
        }

        return path;
    }

    public @NotNull String getPresentableName() {
        return this.myHomePath;
    }

    public boolean isValid() {
        return myVersion != null && FactorioUtil.checkForFactorio(this.myHomePath);
    }

    public String getSystemIndependentPath() {
        return myHomePath;
    }

    public FactorioVersion getVersion() {
        return myVersion;
    }
}
