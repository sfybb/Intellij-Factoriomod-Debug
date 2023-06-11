package factorio.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.search.ExecutionSearchScopes;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.intellij.xdebugger.stepping.XSmartStepIntoHandler;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.DAPEventNames;
import factorio.debugger.DAP.messages.events.DAPBreakpointEvent;
import factorio.debugger.DAP.messages.events.DAPModuleEvent;
import factorio.debugger.DAP.messages.events.DAPOutputEvent;
import factorio.debugger.DAP.messages.events.DAPStoppedEvent;
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest;
import factorio.debugger.DAP.messages.response.DAPCompletionsResponse;
import factorio.debugger.DAP.messages.response.DAPEvaluateResponse;
import factorio.debugger.DAP.messages.response.DAPScopesResponse;
import factorio.debugger.DAP.messages.response.DAPSetExpressionResponse;
import factorio.debugger.DAP.messages.response.DAPStackTraceResponse;
import factorio.debugger.DAP.messages.response.DAPStepInTargetsResponse;
import factorio.debugger.DAP.messages.response.DAPThreadsResponse;
import factorio.debugger.DAP.messages.response.DAPVariablesResponse;
import factorio.debugger.DAP.messages.types.DAPBreakpoint;
import factorio.debugger.DAP.messages.types.DAPCapabilities;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint;
import factorio.debugger.DAP.messages.types.DAPThread;
import factorio.debugger.DAP.messages.types.DAPVariable;
import factorio.debugger.actions.FactorioExceptionBreakpointAction;
import factorio.debugger.breakpoint.FactorioLineBreakpointHandler;
import factorio.debugger.frames.FactorioSourcePosition;
import factorio.debugger.frames.FactorioStackFrame;
import factorio.debugger.frames.FactorioVariableContainer;
import factorio.debugger.game.FactorioGameRuntimeEnvironment;

public class FactorioDebugProcess extends XDebugProcess {
    private final FactorioSmartStepIntoHandler mySmartStepIntoHandler;
    private final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(FactorioDebugProcess.class);
    private final KillableProcessHandler myProcessHandler;
    private final FactorioDebuggerEditorsProvider myEditorsProvider;
    private final ExecutionConsole myExecutionConsole;
    private final FactorioGameRuntimeEnvironment myFactorioGameRuntimeEnv;
    private final ConsoleView myDebugeeConsole;
    private final FactorioDebugger myDebugger;
    private XBreakpointHandler<?>[] myBreakpointHandlers;
    private final FactorioLocalPositionConverter myPositionConverter;
    private @Nullable Pair<Integer, XSourcePosition> runToPositionBreakpoint;

    private final Map<Integer, XLineBreakpoint<?>> idToBreakpointMap;

    /**
     * @param session pass {@code session} parameter of {@link XDebugProcessStarter#start} method to this constructor
     */
    protected FactorioDebugProcess(@NotNull final XDebugSession session,
                                   @Nullable ProcessHandler processHandler,
                                   @NotNull FactorioGameRuntimeEnvironment factorioGameRuntimeEnv,
                                   @NotNull ExecutionConsole executionConsole) {
        super(session);
        /** TODO {@link com.intellij.javascript.debugger.scripts.SourceTabManager#addScript} */
        this.myProcessHandler = (KillableProcessHandler) processHandler;
        this.myFactorioGameRuntimeEnv = factorioGameRuntimeEnv;
        this.myEditorsProvider = new FactorioDebuggerEditorsProvider();
        this.myExecutionConsole = executionConsole;
        this.idToBreakpointMap = new HashMap<>();
        this.myPositionConverter = new FactorioLocalPositionConverter();
        this.mySmartStepIntoHandler = new FactorioSmartStepIntoHandler(this);
        session.setPauseActionSupported(true);

        GlobalSearchScope scope = ExecutionSearchScopes.executionScope(session.getProject(), session.getRunProfile());
        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(session.getProject(), scope);
        this.myDebugeeConsole = builder.getConsole();
        Disposer.register(executionConsole, this.myDebugeeConsole);

        this.runToPositionBreakpoint = null;
        this.myDebugger = new FactorioDebugger(this);

        if(this.myProcessHandler != null) {
            this.myProcessHandler.addProcessListener(this.myDebugger.getSocket());
            this.myProcessHandler.addProcessListener(new ProcessListener() {
                @Override public void startNotified(@NotNull ProcessEvent event) {}
                @Override public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {}

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    if(!FactorioDebugProcess.this.myDebugger.wasTerminationRequested() && event.getExitCode() != 0) {
                        String errorMessage = FactorioDebugProcess.this.myDebugger.getLastReceivedMessage();
                        FactorioDebugProcess.this.myDebugeeConsole.print(String.format("Last message from FMTK:\n%s", errorMessage), ConsoleViewContentType.LOG_ERROR_OUTPUT);
                        FactorioDebugProcess.this.getSession().reportError("Debugee terminated unexpectedly");
                    }
                }
            });
        }

        session.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionStopped() {
                XDebugSessionListener.super.sessionStopped();
            }
        });

        this.myDebugger.setEventHandler(DAPEventNames.STOPPED, stoppedEvent -> {
            if(stoppedEvent != null) {
                this.myDebugger.getThreads().onProcessed(threads -> FactorioDebugProcess.this.positionReached((DAPStoppedEvent) stoppedEvent, threads));
            }
        });
        this.myDebugger.setEventHandler(DAPEventNames.OUTPUT, this::debugeeOutput);
        this.myDebugger.setEventHandler(DAPEventNames.MODULE, event -> this.myPositionConverter.addModule((DAPModuleEvent)event));
        this.myDebugger.setEventHandler(DAPEventNames.INITIALIZED , init_msg -> {
            FactorioDebugProcess.this.initialize(FactorioDebugProcess.this.myDebugger.getCapabilities(), init_msg.sequence);
        });
        this.myDebugger.setEventHandler(DAPEventNames.BREAKPOINT, this::handleBreakpointEvent);
        this.myDebugger.setEventHandler(DAPEventNames.TERMINATED, ignored -> {
            logger.info(String.valueOf(ignored));
            this.getSession().stop();
        });
    }

    private void tryAddBreakpointTypesByClassPath(List<XBreakpointHandler<?>> breakpointHandlers, String[] breakpointIds) {
        for(String bpId : breakpointIds) {
            final XBreakpointType<?, ?> breakpointType = XBreakpointUtil.findType(bpId);
            if(breakpointType != null) {
                breakpointHandlers.add(new FactorioLineBreakpointHandler(this, breakpointType.getClass()));
            } else {
                logger.info(String.format("Cannot add breakpoint type '%s'", bpId));
            }
        }
    }

    private void initialize(final DAPCapabilities capabilities, int receive_sequence) {
        logger.info("Connected debug adapter capabilities: " +
            capabilities.getCapabilities().stream().map(Enum::name)
                .collect(Collectors.joining(", ", "[", "]")));

        List<XBreakpointHandler<?>> breakpointHandlers = new ArrayList<>();

        /*if (capabilities == null) {

        }*/
        //breakpointHandlers.add(new FactorioExceptionBreakpointHandler(this)); "javascript-exception"
        tryAddBreakpointTypesByClassPath(breakpointHandlers, new String[]{"lua-line", "javascript"});

        this.myBreakpointHandlers = breakpointHandlers.toArray(XBreakpointHandler.EMPTY_ARRAY);

        this.myDebugger.whenPreviousEventsProcessed(receive_sequence).onProcessed(ignored -> {
            FactorioDebugProcess.this.logger.warn("Requesting breakpoint initialization");
            ApplicationManager.getApplication().runReadAction(() -> {
                FactorioDebugProcess.this.getSession().initBreakpoints();
                FactorioDebugProcess.this.myDebugger.processAddedBreakpoints(true);
                if (hasCapability(DAPCapabilitiesEnum.ConfigurationDoneRequest)) {
                    FactorioDebugProcess.this.myDebugger.configurationDone();
                }
            });
        });
    }

    public DAPCapabilities getCapabilities() {
        return this.myDebugger.getCapabilities();
    }

    public boolean hasCapability(DAPCapabilitiesEnum capability) {
        return getCapabilities().has(capability);
    }

    @Override
    public void registerAdditionalActions(@NotNull final DefaultActionGroup leftToolbar, @NotNull final DefaultActionGroup topToolbar, @NotNull final DefaultActionGroup settings) {
        leftToolbar.addAction(new FactorioExceptionBreakpointAction(), new Constraints(Anchor.AFTER, "XDebugger.MuteBreakpoints"));
    }

    private void debugeeOutput(@Async.Execute final DAPEvent dapEvent) {
        DAPOutputEvent outputEvent = (DAPOutputEvent) dapEvent;

        if (outputEvent.body.output == null || outputEvent.body.category == null) {
            logger.warn("Received invalid output event!");
            return;
        }

        ConsoleViewContentType contentType = switch (outputEvent.body.category) {
            case IMPORTANT -> ConsoleViewContentType.LOG_ERROR_OUTPUT;
            case CONSOLE -> ConsoleViewContentType.LOG_INFO_OUTPUT;
            case STDOUT -> ConsoleViewContentType.NORMAL_OUTPUT;
            case STDERR -> ConsoleViewContentType.ERROR_OUTPUT;
            case TELEMETRY -> ConsoleViewContentType.LOG_DEBUG_OUTPUT;
        };

        if((contentType != ConsoleViewContentType.NORMAL_OUTPUT || contentType != ConsoleViewContentType.ERROR_OUTPUT) &&
            !outputEvent.body.output.endsWith("\n")) {
            outputEvent.body.output = outputEvent.body.output + "\n";
        }

        if (ConsoleViewContentType.SYSTEM_OUTPUT != contentType) this.myDebugeeConsole.print(outputEvent.body.output, contentType);
        else logger.warn("Debugger output: " + outputEvent.body.category.name() + " " + outputEvent.body.output);
    }

    private void positionReached(@NotNull final DAPStoppedEvent stoppedEvent, @NotNull final DAPThreadsResponse threadInfo) {
        logger.warn("Stopped execution: "+stoppedEvent);

        Promise<XSuspendContext> suspendContext = createSuspendContext(stoppedEvent, threadInfo);

        final DAPStoppedEvent.StoppedEventBody stopped = stoppedEvent.body;

        Promise<XLineBreakpoint<?>> tmpHitBP = null;
        if (stopped.reason == DAPStoppedEvent.StoppedEventBody.SoppedReason.BREAKPOINT ||
            stopped.reason == DAPStoppedEvent.StoppedEventBody.SoppedReason.FUNCTION_BREAKPOINT ||
            stopped.reason == DAPStoppedEvent.StoppedEventBody.SoppedReason.DATA_BREAKPOINT ||
            stopped.reason == DAPStoppedEvent.StoppedEventBody.SoppedReason.INSTRUCTION_BREAKPOINT) {
            // Breakpoint hit

            boolean isRTPBreakpoint = false; // is this the "runToPosition" helper breakpoint?

            Integer[] hitBreakpointIds = stopped.hitBreakpointIds;
            if(hitBreakpointIds != null) {
                for (final Integer hitBreakpointId : hitBreakpointIds) {
                    if (runToPositionBreakpoint != null &&
                        Objects.equals(runToPositionBreakpoint.first, hitBreakpointId)) {
                        isRTPBreakpoint = true;
                        break;
                    }
                }


                for (final Integer hitBreakpointId : hitBreakpointIds) {
                    XLineBreakpoint<?> bp = idToBreakpointMap.get(hitBreakpointId);
                    if(bp != null) {
                        tmpHitBP = Promises.resolvedPromise(bp);
                        break;
                    }
                }
            }

            if(isRTPBreakpoint) {
                tmpHitBP = Promises.rejectedPromise();
                removeBreakpoint(runToPositionBreakpoint.second, null);
                runToPositionBreakpoint = null;
            }


            if(tmpHitBP == null && !isRTPBreakpoint) {
                // Try to find bp by source position
                AsyncPromise<XLineBreakpoint<?>> foundLineBp = new AsyncPromise<>();
                tmpHitBP = foundLineBp;
                suspendContext = suspendContext.then(suspendCtx -> {
                    XExecutionStack[] stacks = suspendCtx.getExecutionStacks();
                    XStackFrame topFrame = stacks.length > 0 && stacks[0] != null ? stacks[0].getTopFrame() : null;
                    XSourcePosition sourcePosition = topFrame != null ? topFrame.getSourcePosition() : null;

                    if(sourcePosition != null) {
                        if (runToPositionBreakpoint != null && runToPositionBreakpoint.second != null &&
                            Objects.equals(runToPositionBreakpoint.second.getFile().getUrl(), sourcePosition.getFile().getUrl()) &&
                            runToPositionBreakpoint.second.getLine() == sourcePosition.getLine()) {
                            // We hit a "runToPosition" breakpoint
                            removeBreakpoint(runToPositionBreakpoint.second, null);
                            runToPositionBreakpoint = null;
                            foundLineBp.cancel();
                        } else {
                            XLineBreakpoint<?> lineBp = findBreakpoint(sourcePosition);
                            if (lineBp != null)
                                foundLineBp.setResult(lineBp);
                            else
                                foundLineBp.setError(String.format("No breakpoint found in %s line %d", sourcePosition.getFile().getUrl()
                                    , sourcePosition.getLine()));
                        }
                    } else {
                        foundLineBp.setError("No source position");
                    }
                    return suspendCtx;
                });
            }
        } else {
            tmpHitBP = Promises.rejectedPromise();
        }

        final Promise<XLineBreakpoint<?>> hitBP = tmpHitBP;

        suspendContext.onSuccess(suspendCtx -> {
            hitBP.onSuccess(myHitBP -> {
                boolean shouldSuspend = this.getSession().breakpointReached(myHitBP, null, suspendCtx);
                if (!shouldSuspend)
                    this.resume(suspendCtx);
            }).onError(
                err -> {
                    XDebugSession session = FactorioDebugProcess.this.getSession();
                    if(session instanceof XDebugSessionImpl) ((XDebugSessionImpl) session).positionReached(suspendCtx, true);
                    else session.positionReached(suspendCtx);
                });
        }).onError(err -> {
            logger.warn(String.format("Unable to stop because suspend context creation failed with: %s", err));
            FactorioDebugProcess.this.resume(null);
        });
    }

    private Promise<XSuspendContext> createSuspendContext(@NotNull DAPStoppedEvent stoppedEvent, @NotNull DAPThreadsResponse threadInfo) {
        if(stoppedEvent.body.allThreadsStopped != null && stoppedEvent.body.allThreadsStopped) {
            return FactorioSuspendContext.fromThreads(threadInfo.body.threads, this);
        } else {
            DAPThread[] threads = new DAPThread[1];
            for(DAPThread th : threadInfo.body.threads) {
                if(Objects.equals(th.id, stoppedEvent.body.threadId)) {
                    threads[0] = th;
                    break;
                }
            }

            return FactorioSuspendContext.fromThreads(threads, this);
        }
    }

    private @Nullable XLineBreakpoint<?> findBreakpoint(@NotNull XSourcePosition sourcePosition) {
        for (final Map.Entry<Integer, XLineBreakpoint<?>> integerXLineBreakpointEntry : idToBreakpointMap.entrySet()) {
            XLineBreakpoint<?> bp = integerXLineBreakpointEntry.getValue();
            if(Objects.equals(bp.getFileUrl(), sourcePosition.getFile().getUrl()) &&
                bp.getLine() == sourcePosition.getLine()) {
                return bp;
            }
        }
        return null;
    }

    @Override
    public boolean checkCanInitBreakpoints() {
        return this.myDebugger.isInitialized();
    }

    @Override
    public void sessionInitialized() {
        FactorioDebugProcess.this.myDebugger.launch();
    }

    @Override
    protected @Nullable ProcessHandler doGetProcessHandler() {
        return this.myProcessHandler;
    }

    @Override
    public @NotNull ExecutionConsole createConsole() {
        return this.myDebugeeConsole;
    }

    @Override
    public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
        return this.myBreakpointHandlers;
    }

    @Override
    public @NotNull XDebuggerEditorsProvider getEditorsProvider() {
        return this.myEditorsProvider;
    }

    @Override
    public @Nullable XDebuggerEvaluator getEvaluator() {
        XStackFrame frame = this.getSession().getCurrentStackFrame();
        return frame != null ? frame.getEvaluator() : null;
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        this.myDebugger.stepOver();
    }
    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        this.myDebugger.stepOut();
    }

    @Override
    public void startStepInto(@Nullable final XSuspendContext context) {
        this.myDebugger.stepInto(null);
    }

    @Override
    public @Nullable XSmartStepIntoHandler<FactorioSmartStepIntoVariant> getSmartStepIntoHandler() {
        if(this.hasCapability(DAPCapabilitiesEnum.StepInTargetsRequest)) {
            return mySmartStepIntoHandler;
        }
        return null;
    }

    @Override
    public @NotNull Promise<Object> stopAsync() {
        if (!this.hasCapability(DAPCapabilitiesEnum.TerminateRequest)) {
            this.myDebugger.setTerminating();
            return Promises.rejectedPromise();
        }
        return this.myDebugger.stop().then(r -> r);
    }

    @Override
    public void startPausing() {
        this.myDebugger.startPausing();
    }

    @Override
    public void resume(@Nullable final XSuspendContext context) {
        this.myDebugger.resume();
    }

    @Override
    public void runToPosition(@NotNull final XSourcePosition position, @Nullable final XSuspendContext context) {
        //runToPositionBreakpoint = new Pair<>(-1, position);

        addInvisibleTemporaryBreakpoint(position)
            .onSuccess(rtpBp -> {
                Object id = rtpBp.additionalProperties.get("id");
                if(id instanceof Integer) {
                    runToPositionBreakpoint = new Pair<>((Integer) id, position);
                    resume(context);
                }
            })
            .onError(err -> {
                this.getSession().reportError("Unable to run to position: failed to create helper breakpoint: "+ err);
            });
    }

    public void reregisterBreakpoints() {
        ((FactorioLineBreakpointHandler)this.myBreakpointHandlers[0]).reregisterBreakpoints();
    }

    private Promise<DAPSourceBreakpoint> addInvisibleTemporaryBreakpoint(final XSourcePosition position) {
        XSourcePosition convertedPosition = this.myPositionConverter.convertToFactorio(position);
        // TODO check for acceptable breakpoint locations
        return this.myDebugger.addBreakpoint(convertedPosition, null);
    }

    public void addBreakpoint(final XSourcePosition position, final XLineBreakpoint<?> breakpoint) {
        XSourcePosition convertedPosition = this.myPositionConverter.convertToFactorio(position);
        this.myDebugger.addBreakpoint(convertedPosition, breakpoint)
            .onSuccess(bpResult ->
                handleBreakpointResponse(bpResult, breakpoint))
            .onError(error ->
                handleBreakpointResponse(null, breakpoint));
    }

    public void removeBreakpoint(final XSourcePosition position, final XLineBreakpoint<?> breakpoint) {
        XSourcePosition convertedPosition = this.myPositionConverter.convertToFactorio(position);
        this.myDebugger.removeBreakpoint(convertedPosition, breakpoint);
    }

    private void handleBreakpointResponse(@Nullable DAPSourceBreakpoint bpResponse, XLineBreakpoint<?> breakpoint) {
        if(bpResponse != null) {
            Object id = bpResponse.additionalProperties.get("id");
            if(id instanceof Integer) idToBreakpointMap.put((Integer) id, breakpoint);
            if(Boolean.TRUE.equals(bpResponse.additionalProperties.get("verified"))) {
                FactorioDebugProcess.this.getSession().setBreakpointVerified(breakpoint);
                return;
            }
        }
        FactorioDebugProcess.this.getSession().setBreakpointInvalid(breakpoint, "Unable to verify");
    }

    private void handleBreakpointEvent(@Async.Execute final DAPEvent dapEvent) {
        DAPBreakpointEvent breakpointEvent = (DAPBreakpointEvent) dapEvent;
        if(breakpointEvent == null || breakpointEvent.body == null) return;

        String reason = breakpointEvent.body.reason;
        DAPBreakpoint bp = breakpointEvent.body.breakpoint;

        if(bp != null && bp.id != null) {
            XLineBreakpoint<?> lineBP = idToBreakpointMap.get(bp.id);
            if(lineBP != null) {
                if(bp.verified) {
                    this.getSession().setBreakpointVerified(lineBP);
                } else {
                    this.getSession().setBreakpointInvalid(lineBP, bp.message != null ? bp.message : "Unable to verify");
                }
            }
        }
    }

    public XSourcePosition getSourcePosition(final FactorioSourcePosition position) {
        if (position == null || position.getFile() == null) return null;

        return this.myPositionConverter.convertFromFactorio(position, this.getFactorioBaseDir());
    }

    public Promise<DAPScopesResponse> getScope(final int frameId) {
        return this.myDebugger.getScope(frameId);
    }

    public Promise<DAPVariablesResponse> getVariable(int varRev, final int offset, final int maxResults) {
        return this.myDebugger.getVariable(varRev, offset, maxResults);
    }

    public String getFactorioBaseDir() {
        return myFactorioGameRuntimeEnv.getBasePath();
    }

    public Promise<DAPStackTraceResponse> getStackTrace(final int id) {
        return this.myDebugger.getStackTrace(id);
    }

    public String toJSON(final Object dapMsg) {
        return this.myDebugger.toJSON(dapMsg);
    }

    public Promise<DAPEvaluateResponse> evaluate(@NotNull final String expression,
                                                 final int frameId,
                                                 @Nullable final DAPEvaluateRequest.EvalContext context) {
        return this.myDebugger.evaluate(expression, frameId, context);
    }

    public Promise<DAPCompletionsResponse> getCompletions(final String text, final int myFrameId, final int line, final int column) {
        return this.myDebugger.getCompletions(text, myFrameId, line, column);
    }

    public Promise<DAPStepInTargetsResponse> getStepIntoTargets(@NotNull final FactorioStackFrame frameId) {
        return this.myDebugger.getStepIntoTargets(frameId.getStackFrameId());
    }

    public void stepInto(final FactorioSmartStepIntoVariant variant) {
        this.myDebugger.stepInto(variant.getTargetId());
    }

    public Promise<DAPVariable> setValue(final @Nullable FactorioVariableContainer myParent, final @NotNull String myVariableName, final String expression) {
        Promise<DAPVariable> res = this.setExpression(myVariableName, expression, this.getCurrentFrame())
                .then(evalRes -> evalRes != null ? evalRes.body.toVariable() : null);

        if(res.getState() == Promise.State.REJECTED && !this.hasCapability(DAPCapabilitiesEnum.SetVariable)) {
            return Promises.rejectedPromise("Operation not supported");
        }

        if (myParent == null) {
            return Promises.rejectedPromise("This variable cant be changed because it has no parent!");
        }

        return this.myDebugger.setValue(myParent.getReferenceId(), myVariableName, expression)
            .then(setValRes -> setValRes != null ? setValRes.body.toVariable() : null);
    }

    private Promise<DAPSetExpressionResponse> setExpression(@NotNull final String evaluationExpression,
                                                            @NotNull final String expression,
                                                            @Nullable final FactorioStackFrame currentFrame) {
        if(!this.hasCapability(DAPCapabilitiesEnum.SetExpression)) {
            return Promises.rejectedPromise("Operation not supported");
        }
        return this.myDebugger.setExpression(evaluationExpression, expression, currentFrame != null ? currentFrame.getStackFrameId() : null);
    }

    private @Nullable FactorioStackFrame getCurrentFrame() {
        XSuspendContext ctx = this.getSession().getSuspendContext();
        XExecutionStack stack = ctx != null? ctx.getActiveExecutionStack() : null;
        XStackFrame frame = stack != null ? stack.getTopFrame() : null;
        return frame instanceof FactorioStackFrame ? (FactorioStackFrame) frame : null;
    }
}
