package factorio.debugger.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

public final class FactorioUtil {
    private static final Logger LOG = Logger.getInstance(FactorioUtil.class);

    public static boolean checkForFactorio(@NotNull String homePath) {
        return checkForFactorio(Path.of(homePath));
    }

    private static boolean checkForFactorio(final @NotNull Path homePath) {
        return (Files.exists(homePath.resolve("bin/x64/factorio")) ||  Files.exists(homePath.resolve("bin/x64/factorio.exe"))) &&
            Files.isDirectory(homePath.resolve("data/base")) &&
            Files.isDirectory(homePath.resolve("data/core")) &&
            Files.exists(homePath.resolve("doc-html/runtime-api.json"));
    }


    @Nullable
    public static FactorioVersion getVersion(final @NotNull String sdkHome) {
        Path baseInfoFile = Paths.get(sdkHome, "data/base/info.json");

        if (Files.isRegularFile(baseInfoFile)) {
            try {
                JsonNode baseInfoJson = (new ObjectMapper()).readTree(baseInfoFile.toFile());

                String versionStr = baseInfoJson.at("/version").asText("");
                if(!versionStr.isEmpty()) {
                    return FactorioVersion.parse(versionStr);
                }
            } catch (IOException | IllegalArgumentException e) {
                LOG.info(baseInfoFile.toString(), e);
            }
        }

        return null;
    }

    @Nullable
    public static String getVersionString(final @NotNull String sdkHome) {
        FactorioVersion version = getVersion(sdkHome);
        return version != null ? version.toString() : null;
    }

    public static @Nullable String getHomePathFromExec(final String execPathStr) {
        Path execPath = Path.of(execPathStr);
        if(Files.isExecutable(execPath)) {
            Path homePath = execPath.getParent().resolve(Path.of("..", "..")).normalize().toAbsolutePath();
            return checkForFactorio(homePath) ? homePath.toString() : null;
        }
        return null;
    }
}
