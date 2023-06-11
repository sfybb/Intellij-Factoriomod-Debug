package factorio.debugger.runconfig;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.LogFileOptions;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.io.BaseDataReader;
import com.intellij.util.io.BaseOutputReader;
import com.intellij.xdebugger.XDebuggerManager;
import factorio.debugger.FactorioDebugProcess;
import factorio.debugger.game.FactorioCommandLineConfigurator;
import factorio.debugger.game.FactorioFMTKRuntimeEnvironment;
import factorio.debugger.game.FactorioGameRuntimeEnvironment;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;

public class FactorioRunProfileState extends CommandLineState {
    private final FactorioRunConfigurationOptions myOptions;
    private @Nullable final NodeJsInterpreter myNodeJsInterpreter;
    private @Nullable final FactorioFMTKRuntimeEnvironment myFMTKEnv;
    private final FactorioGameRuntimeEnvironment myFactorioGameEnv;
    private final FactorioRunConfiguration myRunConfiguration;
    private final Project myProject;
    private final boolean isDebug;

    public FactorioRunProfileState(@NotNull final Executor executor,
                                   @NotNull final ExecutionEnvironment environment,
                                   @NotNull final FactorioRunConfiguration runConfiguration) {
        super(environment);
        isDebug = "Debug".equals(executor.getId());
        myRunConfiguration = runConfiguration;
        myProject = runConfiguration.getProject();
        myOptions = runConfiguration.getOptions();

        myNodeJsInterpreter = NodeJsInterpreterRef.create(myOptions.getNodeJsInterpreterRef()).resolve(myProject);
        myFactorioGameEnv = new FactorioRuntimeEnvironmentRef(myOptions.getFactorioRuntimeRef()).resolveAsFactorioGame();
        myFMTKEnv = new FactorioRuntimeEnvironmentRef(myOptions.getFMTKPackage()).resolveAsFMTK();
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        if(myFactorioGameEnv == null || !myFactorioGameEnv.isValid() ||
            isDebug && (myFMTKEnv == null || !myFMTKEnv.isValid() || myNodeJsInterpreter == null)) {

            StringBuilder sb = new StringBuilder();
            if(myFactorioGameEnv == null) {
                sb.append("Factorio Game executable is undefined");
            } else if (!myFactorioGameEnv.isValid()) {
                sb.append("Factorio Game path is not a valid Factorio installation");
            }

            if(isDebug) {
                if(myFMTKEnv == null) {
                    if(sb.length() > 0) sb.append("\n");
                    sb.append("No FMTK package specified");
                } else if ( !myFMTKEnv.isValid()) {
                    if(sb.length() > 0) sb.append("\n");
                    sb.append("Specified package is not FMKT");
                }

                if(myNodeJsInterpreter == null) {
                    if(sb.length() > 0) sb.append("\n");
                    sb.append("No NodeJs interpreter specified");
                }
            }

            throw new ExecutionException(sb.toString());
        }

        if (isDebug) {
            addLogfiles();
        }

        GeneralCommandLine commandLine = optionsToCommandLine();

        OSProcessHandler processHandler;
        if (isDebug) {
            processHandler = new KillableProcessHandler(commandLine) {
                @Override
                protected BaseOutputReader.@NotNull Options readerOptions() {
                    return new BaseOutputReader.Options() {
                        public BaseDataReader.SleepingPolicy policy() {
                            return BaseDataReader.SleepingPolicy.BLOCKING;
                        }
                        @Override
                        public boolean splitToLines() { return false; }
                    };
                }

                @Override
                protected boolean destroyProcessGracefully() {
                    List<? extends FactorioDebugProcess> debugProcesses = XDebuggerManager.getInstance(myProject).getDebugProcesses(FactorioDebugProcess.class);

                    for (final FactorioDebugProcess debugProcess : debugProcesses) {
                        if(debugProcess.getProcessHandler().equals(this)) {
                            return debugProcess.stopAsync().getState() != Promise.State.REJECTED;
                        }
                    }
                    return false;
                }
            };

            //TODO additional tabs?
            myRunConfiguration.createAdditionalTabComponents(null, processHandler);
        } else {
            processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
            ProcessTerminatedListener.attach(processHandler);
        }

        return processHandler;
    }

    public FactorioGameRuntimeEnvironment getFactorioGameEnv() {
        return myFactorioGameEnv;
    }

    private GeneralCommandLine optionsToCommandLine() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        if (isDebug) {
            String debugNodeArgs = myOptions.getDebugNodeArgs();
            if(!StringUtil.isEmptyOrSpaces(debugNodeArgs)) {
                debugNodeArgs = debugNodeArgs.trim();
                for (final String s : debugNodeArgs.split("\\s")) {
                    if(!StringUtil.isEmptyOrSpaces(s)) {
                        commandLine.addParameter(s);
                    }
                }
            }

            FactorioCommandLineConfigurator.configure(
                myFMTKEnv, myFactorioGameEnv,
                myNodeJsInterpreter, commandLine);
        } else {
            FactorioCommandLineConfigurator.configure(myFactorioGameEnv, commandLine);
        }

        return commandLine;
    }

    private void addLogfiles() {
        myOptions.getLogFiles().clear();

        String logfilePath = Path.of(System.getProperty("java.io.tmpdir"), "fmtk.log").normalize().toString();
        myOptions.getLogFiles().add(new LogFileOptions("fmtk.log", logfilePath, isTraceEnabled(), false, true));
    }

    private boolean isTraceEnabled() {
        return myOptions.getTrace();
    }
}
