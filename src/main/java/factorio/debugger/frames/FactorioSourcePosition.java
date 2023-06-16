package factorio.debugger.frames;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import factorio.debugger.FactorioLocalPositionConverter;
import factorio.debugger.game.FactorioMod;

public class FactorioSourcePosition {
    //private final @NotNull FactorioScript script;
    private final Path file;
    private final @Nullable FactorioMod myMod;
    private final int line;
    private final @Nullable XSourcePosition myXSourcePosition;

    private static final String MOD_ID_PREFIX = "@__";
    private static final String MOD_ID_SUFFIX = "__/";

    public FactorioSourcePosition(@NotNull Project project, @Nullable String file, int line,
                                  final FactorioLocalPositionConverter factorioLocalPositionConverter) {
        FactorioMod mod = factorioLocalPositionConverter.getModFromId(this.modIdFromFile(file));
        if (mod == null && file != null) {
            mod = factorioLocalPositionConverter.getModContainingFile(Path.of(file));
        }
        this.file = this.normalize(file);
        this.myMod = mod;
        this.line = line;


        this.myXSourcePosition = factorioLocalPositionConverter.convertFromFactorio(this);
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
        return this.myMod != null ? this.myMod.getId() : null;
    }

    public XSourcePosition getSourcePosition() {
        return this.myXSourcePosition;
    }

    public String getPresentablePath() {
        VirtualFile sourceFile = this.myXSourcePosition != null ? this.myXSourcePosition.getFile() : null;

        String res = null;
        if (myMod != null) {
            res = myMod.getPresentablePath(sourceFile);
        }

        if (res == null) {
            return file != null ? String.valueOf(file) : "";
        }
        return res;
    }

    public int getSourceLine() {
        return this.myXSourcePosition != null ? this.myXSourcePosition.getLine() : getLine();
    }
}
