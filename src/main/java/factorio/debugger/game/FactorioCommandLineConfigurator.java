package factorio.debugger.game;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;

public class FactorioCommandLineConfigurator {

    public static void configure(@NotNull FactorioFMTKRuntimeEnvironment fmtkEnv,
                                 @NotNull FactorioGameRuntimeEnvironment gameEnv,
                                 @NotNull String nodeInterpreter,
                                 @NotNull GeneralCommandLine commandLine) throws ExecutionException {
        commandLine.setExePath(nodeInterpreter);
        commandLine.addParameters(fmtkEnv.getExecuteablePath(), "debug", gameEnv.getExecuteablePath());
    }

    public static void configure(@NotNull FactorioGameRuntimeEnvironment gameEnv, @NotNull GeneralCommandLine commandLine) {
        commandLine.setExePath(gameEnv.getExecuteablePath());
    }
}
