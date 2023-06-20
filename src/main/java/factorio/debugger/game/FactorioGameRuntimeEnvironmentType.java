package factorio.debugger.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

public class FactorioGameRuntimeEnvironmentType extends FactorioRuntimeEnvironmentType<FactorioGameRuntimeEnvironment> {
    private final static FactorioGameRuntimeEnvironmentType myInstance = new FactorioGameRuntimeEnvironmentType();

    public static @NotNull FactorioGameRuntimeEnvironmentType getInstance() {
        return myInstance;
    }

    @Override
    public @NotNull String getName() {
        return "Factorio";
    }

    @Override
    public String getID() {
        return "Factorio";
    }

    @Override
    public FactorioGameRuntimeEnvironment create(@NotNull final String refPath) {
        return new FactorioGameRuntimeEnvironment(refPath);
    }

    @Override
    public @Nullable FactorioGameRuntimeEnvironment findByReferenceName(@NotNull final String refName) {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (executable instanceof FactorioGameRuntimeEnvironment &&
                executable.getSystemIndependentPath().equals(refName))
                return (FactorioGameRuntimeEnvironment) executable;
        }
        return null;
    }

    @Override
    public @NotNull List<FactorioGameRuntimeEnvironment> getEnvironments() {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        List<FactorioGameRuntimeEnvironment> result = new ArrayList<>();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (executable instanceof FactorioGameRuntimeEnvironment)
                result.add((FactorioGameRuntimeEnvironment) executable);
        }
        return result;
    }

    @Override
    public void setEnvironments(@NotNull final List<? extends FactorioRuntimeEnvironment> envs) {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        List<FactorioRuntimeEnvironment> others = new ArrayList<>();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (!(executable instanceof FactorioGameRuntimeEnvironment))
                others.add(executable);
        }

        others.addAll(envs);
        FactorioServiceManager.getInstance().setExecutables(others);
    }

    @Override
    public List<FactorioRuntimeEnvironmentRef> getEnvironmentRefs() {
        final List<FactorioGameRuntimeEnvironment> executables = this.getEnvironments();
        return executables.stream().map(FactorioRuntimeEnvironment::toRef).collect(Collectors.toList());
    }

    @Override
    public FactorioGameRuntimeEnvironment showAddDialog() {
        return chooseInterpreter(null);
    }

    @Override
    public FactorioGameRuntimeEnvironment edit(final FactorioRuntimeEnvironment runtimeEnv) {
        return chooseInterpreter(runtimeEnv);
    }

    @Override
    public String getChooserDialogTitle() {
        return "Factorio game installations";
    }

    @Override
    public String getEnvironmentName() {
        return "Factorio";
    }

    private static @Nullable FactorioGameRuntimeEnvironment chooseInterpreter(@Nullable FactorioRuntimeEnvironment runtimeEnv) {
        // Choose folders or executable files
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, false, false, false, false) {
            @Override
            public boolean isFileSelectable(@Nullable VirtualFile file) {
                return super.isFileSelectable(file) || file != null && SystemInfo.isMac && file.isDirectory() && "app".equals(file.getExtension());
            }
        }.withShowHiddenFiles(SystemInfo.isUnix);;

        if (SystemInfo.isMac) {
            descriptor.setForcedToUseIdeaFileChooser(true);
        }

        VirtualFile initial = null;
        if (runtimeEnv != null) {
            initial = LocalFileSystem.getInstance().findFileByPath(runtimeEnv.getSystemIndependentPath());
        }

        VirtualFile file = FileChooser.chooseFile(descriptor, null, initial);
        return file != null ? new FactorioGameRuntimeEnvironment(file.getPath()) : null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        return o instanceof FactorioGameRuntimeEnvironmentType;
    }
}
