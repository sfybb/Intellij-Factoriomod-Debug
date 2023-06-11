package factorio.debugger.game;

import java.io.IOException;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.util.io.FileUtil;

public class FactorioFMTKRuntimeEnvironment implements FactorioRuntimeEnvironment {
    //private final NodePackage myPackage;
    private final String myPackagePath;
    private final String executableFile;
    private @Nullable final FactorioVersion myVersion;

    public FactorioFMTKRuntimeEnvironment(final @NotNull String packagePath) {
        myPackagePath = FileUtil.toSystemIndependentName(packagePath);
        //myPackage = new NodePackage(myPackagePath);

        ObjectMapper myObjectMapper = new ObjectMapper();
        JsonNode packageJson = null;
        try {
            packageJson = myObjectMapper.readTree(Path.of(myPackagePath, "package.json").toFile());
        } catch (IOException ignored) { }

        if(packageJson != null) {
            myVersion = FactorioVersion.tryParse(packageJson.at("/version").asText(null));
            executableFile = Path.of(packageJson.at("/main").asText("")).normalize().toString();

            if (myVersion != null) {
                loadPackageInfo(packageJson);
            }
        } else {
            executableFile = "";
            myVersion = null;
        }
    }

    private void loadPackageInfo(JsonNode packageJson) {
    }

    @Override
    public @NotNull FactorioRuntimeEnvironmentType<? extends FactorioRuntimeEnvironment> getType() {
        return FactorioFMTKRuntimeEnvironmentType.getInstance();
    }

    @Override
    public @NotNull String getReferenceName() {
        return myPackagePath;
    }

    @Override
    public @NotNull String getPresentableName() {
        return getPresentablePath(myPackagePath);
    }

    @Override
    public @Nullable FactorioVersion getVersion() {
        return myVersion;
    }

    @Override
    public @NotNull String getSystemIndependentPath() {
        return myPackagePath;
    }

    @Override
    public boolean isValid() {
        return FMTKUtil.checkForFMTK(myPackagePath);
    }

    @Override
    public @NotNull String getExecuteablePath() {
        return Path.of(myPackagePath, executableFile).toString();
    }

    @Override
    public FactorioRuntimeEnvironmentRef toRef() {
        return new FactorioRuntimeEnvironmentRef(this.myPackagePath);
    }

    @Override
    public String toString() {
        return getPresentableName();
    }
}
