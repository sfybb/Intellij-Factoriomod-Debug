package factorio.debugger;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import factorio.debugger.runconfig.FactorioRunProfileState;

public class FactorioSessionFactory {

    public @NotNull XDebugSession createSession(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        XDebugSession session = XDebuggerManager.getInstance(environment.getProject()).startSession(environment, new XDebugProcessStarter() {
            @Override
            public @NotNull XDebugProcess start(@NotNull final XDebugSession session) throws ExecutionException {
                return FactorioSessionFactory.this.createDebugProcess(state, environment, session);
            }
        });
        return session;
    }

    private FactorioDebugProcess createDebugProcess(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment, @NotNull final XDebugSession session) throws ExecutionException {
        if (!(state instanceof final FactorioRunProfileState rpf)) throw new ExecutionException("Unable to execute");

        ExecutionResult executionResult = rpf.execute(environment.getExecutor(), environment.getRunner());

        return new FactorioDebugProcess(
            session,
            executionResult.getProcessHandler(),
            rpf.getFactorioGameEnv(),
            executionResult.getExecutionConsole());
    }
}
