package factorio.debugger.runconfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import factorio.debugger.FactorioDebugger;
import factorio.debugger.game.FactorioFMTKRuntimeEnvironment;
import factorio.debugger.game.FactorioGameRuntimeEnvironment;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;

public class FactorioRunConfiguration extends RunConfigurationBase<FactorioRunConfigurationOptions> {
    private Logger logger = Logger.getInstance(FactorioDebugger.class);

    protected FactorioRunConfiguration(@NotNull final Project project, @Nullable final ConfigurationFactory factory, @Nullable final String name) {
        super(project, factory, name);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        FactorioRunConfigurationOptions options = getOptions();
        FactorioGameRuntimeEnvironment factorioRuntimeEnv = new FactorioRuntimeEnvironmentRef(options.getFactorioRuntimeRef()).resolveAsFactorioGame();
        if(factorioRuntimeEnv == null) {
            throw new RuntimeConfigurationException("Missing Factorio game installation!");
        } else if(!factorioRuntimeEnv.isValid()) {
            throw new RuntimeConfigurationException("Invalid Factorio game installation!");
        }

        FactorioFMTKRuntimeEnvironment fmtkRunEnv = new FactorioRuntimeEnvironmentRef(options.getFMTKPackage()).resolveAsFMTK();
        if(fmtkRunEnv == null) {
            throw new RuntimeConfigurationException("Missing Factorio modding toolkit npm package!");
        } else if(!fmtkRunEnv.isValid()) {
            throw new RuntimeConfigurationException("Invalid Factorio modding toolkit package!");
        }

        File nodeJsInterpreter = Path.of(options.getNodeJsInterpreterRef()).toAbsolutePath().toFile();
        if(!nodeJsInterpreter.exists() || !nodeJsInterpreter.isFile() || !Files.isExecutable(Path.of(options.getNodeJsInterpreterRef()))) {
            throw new RuntimeConfigurationException("Missing Node JS interpreter");
        }
        /*String nodeJsError = nodeJsInterpreter.validate(getProject());
        if(nodeJsError != null) {
            throw new RuntimeConfigurationException(nodeJsError);
        }*/
    }

    @NotNull
    @Override
    protected FactorioRunConfigurationOptions getOptions() {
        return (FactorioRunConfigurationOptions) super.getOptions();
    }

    @Override
    public @NotNull final SettingsEditor<FactorioRunConfiguration> getConfigurationEditor() {
        //final SettingsEditorGroup<FactorioRunConfiguration> group = new SettingsEditorGroup<>();
        //group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), );
        //group.addEditor(ExecutionBundle.message("run.configuration.startup.connection.rab.title"), new FactorioStartupSettings(getProject()));
        return new FactorioSettingsEditor(getProject());
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull final Executor executor, @NotNull final ExecutionEnvironment environment) {
        return new FactorioRunProfileState(executor, environment, this);
    }
}
