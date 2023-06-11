package factorio.debugger.game;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.javascript.nodejs.interpreter.NodeCommandLineConfigurator;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;

public class FactorioCommandLineConfigurator {

    public static void configure(@NotNull FactorioFMTKRuntimeEnvironment fmtkEnv,
                                 @NotNull FactorioGameRuntimeEnvironment gameEnv,
                                 @NotNull NodeJsInterpreter interpreter,
                                 @NotNull GeneralCommandLine commandLine) throws ExecutionException {
        NodeCommandLineConfigurator.find(interpreter).configure(commandLine);
        commandLine.addParameters(fmtkEnv.getExecuteablePath(), "debug", gameEnv.getExecuteablePath());
    }

    public static void configure(@NotNull FactorioGameRuntimeEnvironment gameEnv, @NotNull GeneralCommandLine commandLine) {
        commandLine.setExePath(gameEnv.getExecuteablePath());
    }
}
