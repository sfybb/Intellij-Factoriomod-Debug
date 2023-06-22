package factorio.debugger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import factorio.debugger.DAP.messages.events.DAPLoadedSourceEvent;
import factorio.debugger.DAP.messages.events.DAPModuleEvent;
import factorio.debugger.DAP.messages.types.DAPModule;
import factorio.debugger.frames.FactorioSourcePosition;
import factorio.debugger.game.FactorioGameRuntimeEnvironment;
import factorio.debugger.game.FactorioMod;

public class FactorioLocalPositionConverter {
    private final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(FactorioLocalPositionConverter.class);

    private final @NotNull ArrayList<Path> infoJsons;
    private final @NotNull Map<String, FactorioMod> myMods;
    private final @NotNull Project myProject;

    public FactorioLocalPositionConverter(@NotNull Project project) {
        this.myProject = project;
        this.myMods = new HashMap<>();
        this.infoJsons = new ArrayList<>();
        findInfoJsons();
    }

    private void findInfoJsons() {
        Set<String> DIRECTORIES_TO_IGNORE = FactorioMod.Companion.getDIRECTORIES_TO_IGNORE();

        Module[] modules = ProjectUtil.getModules(myProject);
        for (Module module : modules) {
            VirtualFile[] contentRoots = ProjectUtil.getRootManager(module).getContentRoots();

            for (VirtualFile contentRoot : contentRoots) {
                VfsUtilCore.iterateChildrenRecursively(contentRoot,
                    file -> file.isDirectory() ? !DIRECTORIES_TO_IGNORE.contains(file.getName()) : file.getName().equals("info.json"),
                    fileOrDir -> {
                    if (fileOrDir.getName().equals("info.json")) infoJsons.add(fileOrDir.toNioPath());
                    return true;
                });
            }
        }
    }

    public @NotNull FactorioSourcePosition getFactorioSourcePosition(@Nullable String path, int line) {
        return new FactorioSourcePosition(myProject, path, line, this);
    }

    public void addModule(@NotNull DAPModuleEvent moduleEvent, @NotNull FactorioGameRuntimeEnvironment factorioEnv) {
        if(moduleEvent.body.module != null && moduleEvent.body.module.id != null) {
            DAPModule module = moduleEvent.body.module;

            Path projectPath = null;
            String path = module.path != null ? module.path : module.symbolFilePath;
            if (path != null) {
                Path modInfoJson = Path.of(URI.create(path).getPath(), "info.json");
                for (final Path infoJson : infoJsons) {
                    try {
                        if (Files.isSameFile(infoJson, modInfoJson)) {
                            projectPath = infoJson.getParent();
                            break;
                        }
                    } catch (IOException ignored) {}
                }
            }


            FactorioMod mod = FactorioMod.Companion.fromModule(module, projectPath, factorioEnv);
            if (mod != null) {
                myMods.put(mod.getId(), mod);
            }
        }
    }

    public void addScript(@NotNull final DAPLoadedSourceEvent loadedSource) {

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
        if (mod == null) {
            if (!StringUtil.isEmptyOrSpaces(modId))
                logger.warn(String.format("Cannot convert source position '%s':%d to project space: unknown mod with id '%s'", remoteFile, line, modId));
            return null;
        }

        XSourcePosition remoteSourcePosition = XDebuggerUtil.getInstance().createPosition(mod.getSourceFile(remoteFile), line);
        if (remoteSourcePosition == null) return null;

        return mod.convertFactorioToSource(remoteSourcePosition);
    }

    private @Nullable XSourcePosition convertLocalLineToRemote(@NotNull VirtualFile vFile, int line) {
        if(!vFile.isInLocalFileSystem()) return null;

        final Path projectFile = vFile.toNioPath();
        FactorioMod mod = getModContainingFile(projectFile);

        if (mod == null) {
            logger.warn(String.format("Cannot convert source position '%s':%d to factorio space: file is not part of any mod", projectFile, line));
            return null;
        }
        VirtualFile modScriptFile = mod.getFile(projectFile);

        XSourcePosition localSourcePosition = XDebuggerUtil.getInstance().createPosition(modScriptFile, line);
        if (localSourcePosition == null) return null;

        return mod.convertSourceToFactorio(localSourcePosition);


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
