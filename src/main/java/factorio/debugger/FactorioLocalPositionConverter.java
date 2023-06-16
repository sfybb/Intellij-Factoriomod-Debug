package factorio.debugger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import factorio.debugger.DAP.messages.events.DAPModuleEvent;
import factorio.debugger.DAP.messages.types.DAPModule;
import factorio.debugger.frames.FactorioSourcePosition;
import factorio.debugger.game.FactorioGameRuntimeEnvironment;
import factorio.debugger.game.FactorioMod;

public class FactorioLocalPositionConverter {
    private final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(FactorioLocalPositionConverter.class);
    private final @NotNull Map<String, FactorioMod> myMods;
    private final @NotNull Project myProject;

    public FactorioLocalPositionConverter(@NotNull Project project) {
        this.myProject = project;
        this.myMods = new HashMap<>();
    }

    public @NotNull FactorioSourcePosition getFactorioSourcePosition(@Nullable String path, int line) {
        return new FactorioSourcePosition(myProject, path, line, this);
    }

    public void addModule(@NotNull DAPModuleEvent moduleEvent, @NotNull FactorioGameRuntimeEnvironment factorioEnv) {
        if(moduleEvent.body.module != null && moduleEvent.body.module.id != null) {
            DAPModule module = moduleEvent.body.module;
            FactorioMod mod = FactorioMod.Companion.fromModule(module, myProject, factorioEnv);
            if (mod != null) {
                myMods.put(mod.getId(), mod);
            }
        }
    }

    public @Nullable XSourcePosition convertFromFactorio(@NotNull FactorioSourcePosition position) {
        return convertRemoteLineToLocal(position.getFile(), position.getModId(), position.getLine());
    }

    public @NotNull XSourcePosition convertToFactorio(@NotNull XSourcePosition position) {
        XSourcePosition remoteSp = convertLocalLineToRemote(position.getFile(), position.getLine());
        return remoteSp != null ? remoteSp : position;
    }

    private @Nullable XSourcePosition convertRemoteLineToLocal(Path remoteFile, String modId, int line) {
        FactorioMod mod = myMods.get(modId);
        if (mod == null) return null;

        XSourcePosition remoteSourcePosition = XDebuggerUtil.getInstance().createPosition(mod.getSourceFile(remoteFile), line);
        if (remoteSourcePosition == null) return null;

        return mod.convertFactorioToSource(remoteSourcePosition);
    }

    private @Nullable XSourcePosition convertLocalLineToRemote(@NotNull VirtualFile vFile, int line) {
        if(!vFile.isInLocalFileSystem()) return null;

        final Path projectFile = vFile.toNioPath();
        FactorioMod mod = getModContainingFile(projectFile);

        if (mod != null) {
            VirtualFile modScriptFile = mod.getFile(projectFile);

            XSourcePosition localSourcePosition = XDebuggerUtil.getInstance().createPosition(modScriptFile, line);
            if (localSourcePosition == null) return null;

            return mod.convertSourceToFactorio(localSourcePosition);
        }

        return null;
    }

    public @Nullable FactorioMod getModContainingFile(final Path filePath) {
        for (final Map.Entry<String, FactorioMod> modEntry : myMods.entrySet()) {
            FactorioMod mod = modEntry.getValue();
            if (mod.containsFile(filePath)) {
                return mod;
            }
        }
        return null;
    }

    public @Nullable FactorioMod getModFromId(final @Nullable String id) {
        return myMods.get(id);
    }
}
