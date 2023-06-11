package factorio.debugger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.debugger.sourcemap.MappingEntry;
import org.jetbrains.debugger.sourcemap.Mappings;
import org.jetbrains.debugger.sourcemap.SourceMap;
import org.jetbrains.debugger.sourcemap.SourceMapDecoderKt;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.Url;
import com.intellij.util.Urls;
import com.intellij.util.io.PathKt;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import factorio.debugger.DAP.messages.events.DAPModuleEvent;
import factorio.debugger.DAP.messages.types.DAPModule;
import factorio.debugger.frames.FactorioSourcePosition;
import kotlin.Pair;

public class FactorioLocalPositionConverter {
    private static final String[] ZIP_EXTENSIONS = new String[]{".zip"};

    private static final List<String> DIRECTORIES_TO_IGNORE = List.of("node_modules", ".git", ".idea", ".vscode");

    private final Map<String, FactorioModInfo> modules = new HashMap<>();
    private final Map<String, SourceMap> sourceMaps = new HashMap<>();
    private final Map<String, Pair<Mappings, String>> reverseMappings = new HashMap<>();
    private final Map<Path, Path> projectPathToModulePath = new HashMap<>();

    public void addModule(@Async.Execute DAPModuleEvent moduleEvent) {
        if(moduleEvent.body.module != null && moduleEvent.body.module.id != null) {
            DAPModule module = moduleEvent.body.module;
            FactorioModInfo modInfo = new FactorioModInfo(module);
            modules.put(module.id, modInfo);

            if(!modInfo.getModId().equals("#user") && modInfo.getModPath() != null) {
                projectPathToModulePath.put(Path.of(modInfo.getModPath()), Path.of(modInfo.getOriginalPath()));
            }

            precomputeSourceMaps(modInfo);
        }
    }

    private void precomputeSourceMaps(final FactorioModInfo factorioModInfo) {
        if(factorioModInfo.getModPath() == null || factorioModInfo.getModId().equals("#user")) return;
        VirtualFile module = this.getLocalVirtualFile(factorioModInfo.getModPath(), null, null);
        if(module == null || !module.exists() || !module.isDirectory()) return;

        recursiveFindSourceMap(module);
    }

    private void recursiveFindSourceMap(VirtualFile directory) {
        if (directory == null || !directory.isDirectory() || DIRECTORIES_TO_IGNORE.contains(directory.getName())) return;

        for (final VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) recursiveFindSourceMap(child);
            else {
                VirtualFile sourceMapFile = directory.findChild(child.getName() + ".map");
                processSourceMapFile(child.getPath(), sourceMapFile);
            }
        }
    }

    private @Nullable SourceMap processSourceMapFile(String sourceFilePath, final VirtualFile sourceMapFile) {
        if(sourceMapFile == null || !sourceMapFile.exists()) return null;

        SourceMap sourceMap;

        CharSequence fileContent = PathKt.readChars(Paths.get(sourceMapFile.getPath()));
        sourceMap = SourceMapDecoderKt.decodeSourceMapSafely(fileContent, true,
            Urls.newLocalFileUrl(sourceMapFile.getPath()), true);

        if (sourceMap != null) {
            sourceMaps.put(sourceFilePath, sourceMap);

            Url[] sources = sourceMap.getSources();
            for (int i = 0; i < sources.length; i++) {
                final Url sourceUrl = sources[i];
                reverseMappings.put(Paths.get(sourceUrl.getPath()).toAbsolutePath().toString(),
                    new Pair<>(sourceMap.findSourceMappings(i), sourceFilePath));
            }
        }

        return sourceMap;
    }

    public @Nullable XSourcePosition convertFromFactorio(@NotNull FactorioSourcePosition position, String factorioBaseDir) {
        VirtualFile vf = this.getLocalVirtualFile(position.getFile().toString(), position.getModId(), factorioBaseDir);
        return createXSourcePosition(vf, position.getLine());
    }

    public @NotNull XSourcePosition convertToFactorio(@NotNull XSourcePosition position) {
        return createXSourcePositionRemote(position);
    }

    private @NotNull XSourcePosition createXSourcePositionRemote(@NotNull final XSourcePosition position) {
        XSourcePosition mappedPosition = convertLocalLineToRemote(position.getFile(), position.getLine());
        return mappedPosition != null ? mappedPosition : position;
    }

    public @Nullable XSourcePosition createXSourcePosition(@Nullable VirtualFile vFile, int line) {
        VirtualFile sourceFile = null;
        if (vFile != null) {
            SourceMap sourceMap = getSourceMap(vFile);

            if (sourceMap != null) {
                Url sourceFileUrl = sourceMap.getSources().length > 0 ? sourceMap.getSources()[0] : null;

                if (sourceFileUrl != null) {
                    sourceFile = LocalFileSystem.getInstance().findFileByPath(sourceFileUrl.getPath());
                }
            }
        }

        if(sourceFile == null) sourceFile = vFile;

        return vFile != null ? XDebuggerUtil.getInstance().createPosition(sourceFile, convertRemoteLineToLocal(vFile, line)) : null;
    }

    private SourceMap getSourceMap(@NotNull VirtualFile vFile) {
        Path path = Paths.get(vFile.getPath()).normalize().toAbsolutePath();

        SourceMap sourceMap = sourceMaps.get(path.toString());
        if(sourceMap == null) {
            sourceMap = processSourceMapFile(path.toString(), this.getLocalFileSystem().findFileByPath(path+".map"));
        }
        return sourceMap;
    }

    private int convertRemoteLineToLocal(VirtualFile vFile, int line) {
        // fmtk starts lines at 1 but jetbrains starts at 0
        line -= 1;
        if(!vFile.isInLocalFileSystem()) return line;

        SourceMap sourceMap = getSourceMap(vFile);
        return sourceMap != null ? sourceMap.getSourceLineByRawLocation(line, 1) : line;
    }

    private @Nullable XSourcePosition convertLocalLineToRemote(VirtualFile vFile, int line) {
        if(!vFile.isInLocalFileSystem()) return null;

        Pair<Mappings, String> mappings = reverseMappings.get(vFile.getPath());
        if(mappings != null) {
            MappingEntry entry = mappings.getFirst().get(line, 1);

            VirtualFile generatedFile = this.getRemoteVirtualFile(mappings.getSecond());
            return entry != null && generatedFile != null ?
                XDebuggerUtil.getInstance().createPosition(generatedFile, entry.getGeneratedLine() + 1) : null;
        }
        VirtualFile debuggerFile = this.getRemoteVirtualFile(vFile.getPath());
        return debuggerFile != null && debuggerFile.exists() ? XDebuggerUtil.getInstance().createPosition(debuggerFile, line + 1) : null;
    }

    private @Nullable VirtualFile getRemoteVirtualFile(final String projectFile) {
        Path projectPath = Path.of(projectFile);
        for (final Path localBasePath : projectPathToModulePath.keySet()) {
            if(projectPath.startsWith(localBasePath)) {
                Path debuggerPath = projectPathToModulePath.get(localBasePath);
                String remoteFile = debuggerPath.resolve(localBasePath.relativize(projectPath)).toString();
                return this.getVirtualFile(remoteFile, null, null);
            }
        }

        return null;
    }

    private @Nullable VirtualFile getLocalVirtualFile(final @NotNull String remoteFile, @Nullable String modId, @Nullable String factorioBaseDir) {
        Path remotePath = Path.of(remoteFile);
        for (final Map.Entry<Path, Path> pathPathEntry : projectPathToModulePath.entrySet()) {
            Path localPath = pathPathEntry.getKey();
            Path remoteBasePath = pathPathEntry.getValue();
            if(remotePath.startsWith(remoteBasePath)) {
                String localFile = localPath.resolve(remoteBasePath.relativize(remotePath)).toString();
                return this.getVirtualFile(localFile, null, null);
            }
        }

        return this.getVirtualFile(remoteFile, modId, factorioBaseDir);
    }

    public @Nullable VirtualFile getVirtualFile(@NotNull String path, @Nullable  String modId, @Nullable String factorioBaseDir) {
        VirtualFile vFile = this.getLocalFileSystem().findFileByPath(path);
        if (vFile == null && modId != null) {
            vFile = findInModZip(this.getLocalFileSystem(), path, modId, factorioBaseDir);
        }

        return vFile;
    }

    protected VirtualFileSystem getLocalFileSystem() {
        return LocalFileSystem.getInstance();
    }

    public @Nullable VirtualFile findInModZip(@NotNull VirtualFileSystem virtualFileSystem, @NotNull String file,
                                                     @NotNull String modId,
                                                     @Nullable String factorioBaseDir) {
        FactorioModInfo modInfo = modules.get(modId);
        if (modInfo != null) {
            String path = modInfo.getModPath();
            if (path != null) {
                VirtualFile modDir = virtualFileSystem.findFileByPath(path);
                if (modDir != null) {
                    return modDir.findFileByRelativePath(file);
                }
            } else {
                String modPath = modInfo.getModZipPath(factorioBaseDir);
                if (modPath != null) {
                    VirtualFile modZip = virtualFileSystem.findFileByPath(modPath);
                    if (modZip != null) {
                        VirtualFile jarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(modZip);
                        if (jarRoot != null) {
                            return jarRoot.findFileByRelativePath(modInfo.getFilePath(file));
                        }
                    }
                }
            }
        }
        return null;
    }
}
