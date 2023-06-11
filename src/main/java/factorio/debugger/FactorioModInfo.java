package factorio.debugger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.io.FileUtil;
import factorio.debugger.DAP.messages.types.DAPModule;

public class FactorioModInfo {
    private String version;
    private String modId;
    private String path;
    private String originalPath;

    public FactorioModInfo(@NotNull DAPModule module) {
        this.modId = module.id;
        this.version = module.version;
        this.path = module.path != null ? module.path : module.symbolFilePath;
        if(this.path != null) {
            URI uriPath = URI.create(this.path);
            this.originalPath = uriPath.getPath();
            try {
                this.path = String.valueOf(Paths.get(uriPath.getPath()).toRealPath().toAbsolutePath());
            } catch (IOException | InvalidPathException ignored) {
                // ignore
                this.path = uriPath.getPath();
            }
        }
    }

    public String getModPath() {
        return this.path;
    }

    public String getModZipPath(final String factorioBaseDir) {
        if (this.modId == null || this.version == null) return null;
        return FileUtil.normalize(String.format("%s/mods/%s.zip", factorioBaseDir, getModFileName()));
    }

    protected String getModFileName() {
        if (this.modId == null || this.version == null) return "";
        return FileUtil.normalize(String.format("%s_%s", this.modId, this.version));
    }

    public String getModId() {
        return modId;
    }

    public String getFilePath(final String file) {
        return FileUtil.normalize(String.format("%s/%s", getModFileName(), file));
    }

    public String getOriginalPath() {
        return this.originalPath;
    }
}
