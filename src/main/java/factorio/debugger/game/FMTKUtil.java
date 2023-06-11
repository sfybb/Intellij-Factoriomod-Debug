package factorio.debugger.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.util.text.StringUtil;

public class FMTKUtil {

    public static boolean checkForFMTK(@NotNull String packagePath) {
        return checkForFMTK(Path.of(packagePath));
    }

    private static boolean checkForFMTK(final @NotNull Path packagePath) {
        if(!Files.exists(packagePath.resolve("package.json"))) return false;
        String mainJs = "";
        try {
            mainJs = new ObjectMapper().readTree(packagePath.resolve("package.json").toFile()).at("/main").asText("");
        } catch (IOException e) {
            return false;
        }

        return !StringUtil.isEmptyOrSpaces(mainJs) &&
            Files.exists(packagePath.resolve(mainJs));
    }
}
