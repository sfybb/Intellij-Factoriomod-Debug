package factorio.debugger.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

public class FactorioFMTKRuntimeEnvironmentType extends FactorioRuntimeEnvironmentType<FactorioFMTKRuntimeEnvironment> {
    private final static FactorioFMTKRuntimeEnvironmentType myInstance = new FactorioFMTKRuntimeEnvironmentType();

    public static @NotNull FactorioFMTKRuntimeEnvironmentType getInstance() {
        return myInstance;
    }

    @Override
    public @NotNull String getName() {
        return "Factorio modding toolkit";
    }

    @Override
    public String getID() {
        return "FMTK";
    }

    @Override
    public FactorioFMTKRuntimeEnvironment create(@NotNull final String refPath) {
        return new FactorioFMTKRuntimeEnvironment(refPath);
    }

    @Override
    public @Nullable FactorioFMTKRuntimeEnvironment findByReferenceName(@NotNull final String refName) {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (executable instanceof FactorioFMTKRuntimeEnvironment &&
                executable.getSystemIndependentPath().equals(refName))
                return (FactorioFMTKRuntimeEnvironment) executable;
        }
        return null;
    }

    @Override
    public @NotNull List<FactorioFMTKRuntimeEnvironment> getEnvironments() {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        List<FactorioFMTKRuntimeEnvironment> result = new ArrayList<>();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (executable instanceof FactorioFMTKRuntimeEnvironment)
                result.add((FactorioFMTKRuntimeEnvironment) executable);
        }
        return result;
    }

    @Override
    public void setEnvironments(@NotNull final List<? extends FactorioRuntimeEnvironment> envs) {
        final List<FactorioRuntimeEnvironment> executables = FactorioServiceManager.getInstance().getExecutables();
        List<FactorioRuntimeEnvironment> others = new ArrayList<>();
        for (final FactorioRuntimeEnvironment executable : executables) {
            if (!(executable instanceof FactorioFMTKRuntimeEnvironment))
                others.add(executable);
        }

        others.addAll(envs);
        FactorioServiceManager.getInstance().setExecutables(others);
    }

    @Override
    public List<FactorioRuntimeEnvironmentRef> getEnvironmentRefs() {
        final List<FactorioFMTKRuntimeEnvironment> executables = this.getEnvironments();
        return executables.stream().map(FactorioRuntimeEnvironment::toRef).collect(Collectors.toList());
    }

    @Override
    public FactorioFMTKRuntimeEnvironment showAddDialog() {
        return chooseInterpreter(null);
    }

    @Override
    public FactorioFMTKRuntimeEnvironment edit(final FactorioRuntimeEnvironment runtimeEnv) {
        return chooseInterpreter(runtimeEnv);
    }

    @Override
    public String getChooserDialogTitle() {
        return "FMTK packages";
    }

    @Override
    public String getEnvironmentName() {
        return "FMTK";
    }

    private static @Nullable FactorioFMTKRuntimeEnvironment chooseInterpreter(@Nullable FactorioRuntimeEnvironment runtimeEnv) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().withShowHiddenFiles(SystemInfo.isUnix);
            //.createSingleFileNoJarsDescriptor().withShowHiddenFiles(SystemInfo.isUnix);
        if (SystemInfo.isMac) {
            descriptor.setForcedToUseIdeaFileChooser(true);
        }

        VirtualFile initial = null;
        if (runtimeEnv != null) {
            initial = LocalFileSystem.getInstance().findFileByPath(runtimeEnv.getSystemIndependentPath());
        }

        VirtualFile file = FileChooser.chooseFile(descriptor, null, initial);
        return file != null ? new FactorioFMTKRuntimeEnvironment(file.getPath()) : null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        return o instanceof FactorioFMTKRuntimeEnvironmentType;
    }
}
