package factorio.debugger.frames;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.Nullable;

public class FactorioSourcePosition {
    private final Path file;
    private final String modId;
    private final int line;

    private static final String MOD_ID_PREFIX = "@__";
    private static final String MOD_ID_SUFFIX = "__/";

    public FactorioSourcePosition(String file, int line) {
        this.file = this.normalize(file);
        this.modId = this.modIdFromFile(file);
        this.line = line;
    }

    protected @Nullable Path normalize(@Nullable String file) {
        if (file == null) return null;

        // if this is in Uri format the file starts with "file://"
        // but in the standard path format there is no protocol
        // so to discriminate between file path within mod zip and file path to an actual file
        // we check for "@__" at the start

        String pathToNormalize = !file.startsWith(MOD_ID_PREFIX) ? file :
            file.substring(file.indexOf(MOD_ID_SUFFIX)+MOD_ID_SUFFIX.length());


        Path actualPath;
        try {
            actualPath = Paths.get(URI.create(pathToNormalize));
        } catch (IllegalArgumentException ignored) {
            actualPath = Paths.get(pathToNormalize);
        }

        return actualPath.normalize();
    }

    protected @Nullable String modIdFromFile(@Nullable String file) {
        if (file == null || !file.startsWith(MOD_ID_PREFIX)) return null;

        int endIndx = file.indexOf("__/");
        if (endIndx < 3)  return null;

        return file.substring(3, endIndx);
    }

    public int getLine() {
        return this.line;
    }
    public Path getFile() {
        return this.file;
    }

    public String getModId() {
        return this.modId;
    }
}
