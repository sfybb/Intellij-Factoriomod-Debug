package factorio.debugger;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.application.AppUIExecutor;
import com.intellij.xdebugger.XDebugSession;
import factorio.debugger.runconfig.FactorioRunConfiguration;

public class FactorioDebugProgramRunner implements ProgramRunner<RunnerSettings> {
    public static final @NonNls String FACTORIO_DEBUG_RUNNER = "FactorioDebugRunner";

    @Override
    public @NotNull @NonNls String getRunnerId() {
        return FACTORIO_DEBUG_RUNNER;
    }

    @Override
    public boolean canRun(@NotNull final String executorId, @NotNull final RunProfile profile) {
        if (!"Debug".equals(executorId))
            return false;

        return profile instanceof FactorioRunConfiguration;
    }

    @Override
    public void execute(@NotNull final ExecutionEnvironment environment) throws ExecutionException {
        RunProfileState state = environment.getState();

        if (state != null) {
            ExecutionManager.getInstance(environment.getProject()).startRunProfile(environment, () -> {
                FactorioSessionFactory sessionCreator = new FactorioSessionFactory();
                return AppUIExecutor.onWriteThread().submit(() ->
                    sessionCreator.createSession(state, environment)).thenAsync((session) ->
                    AppUIExecutor.onUiThread().submit(() -> {
                        this.initSession(session, state, environment.getExecutor());
                        return session.getRunContentDescriptor();
                }));
            });
        }
    }

    private void initSession(final XDebugSession session, final RunProfileState state, final Executor executor) {

    }
}
