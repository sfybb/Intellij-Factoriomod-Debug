package factorio.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import factorio.debugger.DAP.DAPSocket;
import factorio.debugger.DAP.messages.DAPEventNames;
import factorio.debugger.DAP.messages.events.DAPEvent;
import factorio.debugger.DAP.messages.requests.DAPBreakpointLocationsRequest;
import factorio.debugger.DAP.messages.requests.DAPCompletionsRequest;
import factorio.debugger.DAP.messages.requests.DAPConfigurationDoneRequest;
import factorio.debugger.DAP.messages.requests.DAPContinueRequest;
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest;
import factorio.debugger.DAP.messages.requests.DAPInitializeRequest;
import factorio.debugger.DAP.messages.requests.DAPLaunchRequest;
import factorio.debugger.DAP.messages.requests.DAPNextRequest;
import factorio.debugger.DAP.messages.requests.DAPPauseRequest;
import factorio.debugger.DAP.messages.requests.DAPRequest;
import factorio.debugger.DAP.messages.requests.DAPScopesRequest;
import factorio.debugger.DAP.messages.requests.DAPSetBreakpointsRequest;
import factorio.debugger.DAP.messages.requests.DAPSetExpressionRequest;
import factorio.debugger.DAP.messages.requests.DAPSetVariableRequest;
import factorio.debugger.DAP.messages.requests.DAPStackTraceRequest;
import factorio.debugger.DAP.messages.requests.DAPStepInRequest;
import factorio.debugger.DAP.messages.requests.DAPStepInTargetsRequest;
import factorio.debugger.DAP.messages.requests.DAPTerminateRequest;
import factorio.debugger.DAP.messages.requests.DAPThreadsRequest;
import factorio.debugger.DAP.messages.requests.DAPVariablesRequest;
import factorio.debugger.DAP.messages.responses.DAPBreakpointLocationsResponse;
import factorio.debugger.DAP.messages.responses.DAPCompletionsResponse;
import factorio.debugger.DAP.messages.responses.DAPConfigurationDoneResponse;
import factorio.debugger.DAP.messages.responses.DAPEvaluateResponse;
import factorio.debugger.DAP.messages.responses.DAPInitializeResponse;
import factorio.debugger.DAP.messages.responses.DAPNextResponse;
import factorio.debugger.DAP.messages.responses.DAPResponse;
import factorio.debugger.DAP.messages.responses.DAPScopesResponse;
import factorio.debugger.DAP.messages.responses.DAPSetBreakpointsResponse;
import factorio.debugger.DAP.messages.responses.DAPSetExpressionResponse;
import factorio.debugger.DAP.messages.responses.DAPSetVariableResponse;
import factorio.debugger.DAP.messages.responses.DAPStackTraceResponse;
import factorio.debugger.DAP.messages.responses.DAPStepInResponse;
import factorio.debugger.DAP.messages.responses.DAPStepInTargetsResponse;
import factorio.debugger.DAP.messages.responses.DAPTerminateResponse;
import factorio.debugger.DAP.messages.responses.DAPThreadsResponse;
import factorio.debugger.DAP.messages.responses.DAPVariablesResponse;
import factorio.debugger.DAP.messages.types.DAPBreakpointLocation;
import factorio.debugger.DAP.messages.types.DAPCapabilities;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.DAP.messages.types.DAPScope;
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint;
import factorio.debugger.DAP.messages.types.DAPStackFrame;
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity;

public class FactorioDebugger {
    private final Logger logger = Logger.getInstance(FactorioDebugger.class);
    private final FactorioDebugProcess myFactorioDebugProcess;
    private final DAPSocket myDAPSocket;

    private DAPInitializeResponse initializeResponse;
    private boolean initialized;

    private final CircularFifoBuffer lastMessages;

    private final Map<String, BreakpointsInFile> breakpointsPerFile;
    private boolean batchBreakpointRequests;

    private final boolean linesStartAt1 = true;

    public FactorioDebugger(final FactorioDebugProcess factorioDebugProcess) {
        myFactorioDebugProcess = factorioDebugProcess;
        initialized = false;
        myDAPSocket = new DAPSocket(myFactorioDebugProcess.getProcessHandler().getProcessInput());

        breakpointsPerFile = new HashMap<>();
        batchBreakpointRequests = true;

        lastMessages = new CircularFifoBuffer(20);

        DAPInitializeRequest initializeRequest = new DAPInitializeRequest("factorio-debugger-intellij");

        ApplicationNamesInfo appinfo = ApplicationNamesInfo.getInstance();
        initializeRequest.getArguments().clientID = appinfo.getScriptName();
        initializeRequest.getArguments().clientName = appinfo.getFullProductName();

        // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
        initializeRequest.getArguments().linesStartAt1 = linesStartAt1;
        initializeRequest.getArguments().columnsStartAt1 = true;
        initializeRequest.getArguments().supportsMemoryEvent = true;
        initializeRequest.getArguments().supportsVariableType = true;
        initializeRequest.getArguments().supportsVariablePaging = true;
        initializeRequest.getArguments().supportsInvalidatedEvent = true;
        initializeRequest.getArguments().supportsMemoryReferences = true;
        initializeRequest.getArguments().supportsProgressReporting = true;
        initializeRequest.getArguments().supportsRunInTerminalRequest = true;

        initializeRequest.getArguments().locale = Locale.getDefault().toString().replaceAll("_", "-");
        initializeRequest.getArguments().pathFormat = "path"; // fmtk only supports "paths"

        this.<DAPInitializeRequest.InitializeRequestArguments, DAPInitializeResponse>
            asyncRequest(initializeRequest).onProcessed(this::initialize).onError(logger::error);

    }

    public void setEventHandler(DAPEventNames eventName, Consumer<DAPEvent> consumer) {
        this.myDAPSocket.setEventHandler(eventName, consumer);
    }

    private <D, R extends DAPResponse> Promise<R> asyncRequest(DAPRequest<D> request) {
        return myDAPSocket.sendRequest(request);
    }

    private void initialize(@Async.Execute final DAPResponse response) {
        if (response == null) {
            logger.error("Failed to initialize debug adapter (probably fmtk)");
            throw new RuntimeException();
        }
        this.initializeResponse = (DAPInitializeResponse) response;

        this.myDAPSocket.setCancelRequest(getCapabilities().has(DAPCapabilitiesEnum.CancelRequest));

        this.initialized = true;
    }
    public void launch() {
        DAPLaunchRequest launchRequest = new DAPLaunchRequest();
        launchRequest.getArguments().setOtherField("type", "factoriomod");
        launchRequest.getArguments().setOtherField("request", "launch");
        launchRequest.getArguments().setOtherField("trace", true);
        myDAPSocket.sendRequest(launchRequest);
    }

    public @NotNull DAPCapabilities getCapabilities() {
        DAPCapabilities adapterCapabilities = this.initializeResponse != null ? this.initializeResponse.body : null;
        return adapterCapabilities != null ? adapterCapabilities : DAPCapabilities.EMPTY_CAPABILITIES;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void resume()  {
        this.myDAPSocket.sendRequest(new DAPContinueRequest());
    }

    public Promise<DAPTerminateResponse> stop() {
        Promise<DAPTerminateResponse> stopReqPromise = asyncRequest(new DAPTerminateRequest());

        // resolve the stop promise even if we fail to send the terminate request
        AsyncPromise<DAPTerminateResponse> stopPromise = new AsyncPromise<>();
        stopReqPromise.onProcessed(res -> {
            if(res == null) {
                res = new DAPTerminateResponse();
            }
            stopPromise.setResult(res);
        });

        return stopPromise;
    }

    public Promise<DAPSourceBreakpoint> addBreakpoint(@NotNull final XSourcePosition position, @Nullable final XLineBreakpoint<?> breakpoint) {
        String filePath = position.getFile().getPath();
        if(!this.breakpointsPerFile.containsKey(filePath)) {
            this.breakpointsPerFile.put(filePath, new BreakpointsInFile(linesStartAt1));
        }
        BreakpointsInFile bpInFile = this.breakpointsPerFile.get(filePath);

        if(batchBreakpointRequests) {
            return bpInFile.add(position);
        } else {
            logger.info(String.format("Adding breakpoint to: '%s' %d (src line %d) -- %s",
                position.getFile(), position.getLine(), position.getLine(), breakpoint));
            int bpIndex = bpInFile.addGetIndex(position);
            if(bpIndex != -1) {
                Promise<DAPSetBreakpointsResponse> prom = this.asyncRequest(bpInFile.getSetBreakpointRequest());
                return prom.then(resp -> {
                    if (resp != null && resp.body != null) {
                        if (resp.body.breakpoints.length >= bpIndex) {
                            return resp.body.breakpoints[bpIndex];
                        }
                    }
                    return null;
                });
            } else {
                return Promises.rejectedPromise("Invalid breakpoint");
            }
        }
    }

    public void removeBreakpoint(@NotNull final XSourcePosition position, @Nullable final XLineBreakpoint<?> breakpoint) {
        String filePath = position.getFile().getPath();
        if(!this.breakpointsPerFile.containsKey(filePath)) {
            this.breakpointsPerFile.put(filePath, new BreakpointsInFile(linesStartAt1));
        }
        BreakpointsInFile bpInFile = this.breakpointsPerFile.get(filePath);
        bpInFile.remove(position);

        if(batchBreakpointRequests) {
            return;
        }

        logger.info(String.format("Removing breakpoint from: '%s' %d (src line %d) -- %s",
            position.getFile(), position.getLine(), position.getLine(), breakpoint));
        this.asyncRequest(bpInFile.getSetBreakpointRequest());
    }

    public void processAddedBreakpoints(boolean instantApplyFutureBPs) {
        this.batchBreakpointRequests = !instantApplyFutureBPs;

        for (final Map.Entry<String, BreakpointsInFile> bpEntries : this.breakpointsPerFile.entrySet()) {
            BreakpointsInFile file = bpEntries.getValue();
            logger.info("Adding breakpoints to "+file);
            DAPSetBreakpointsRequest request = file.getSetBreakpointRequest();
            if(request != null) {
                this.<DAPSetBreakpointsRequest.SetBreakpointsArguments, DAPSetBreakpointsResponse>asyncRequest(request)
                    .onProcessed(file::handleSetBreakpointResponse);
            }
        }
    }
    public void startPausing() {
        myDAPSocket.sendRequest(new DAPPauseRequest(0));
    }

    public Promise<DAPStackTraceResponse> getStackTrace(int threadId) {
        DAPStackTraceRequest stackTraceRequest = new DAPStackTraceRequest(threadId);
        // TODO maybe only 20
        stackTraceRequest.getArguments().levels = Integer.MAX_VALUE;
        return asyncRequest(stackTraceRequest).then(resp -> {
            DAPStackTraceResponse stackTrace = (DAPStackTraceResponse) resp;

            if (stackTrace != null && stackTrace.body != null && stackTrace.body.stackFrames != null) {
                for (final DAPStackFrame stackFrame : stackTrace.body.stackFrames) {
                    // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize
                    // request)
                    stackFrame.line -= linesStartAt1 ? 1 : 0;
                }
            }
            return stackTrace;
        });
    }

    public Promise<DAPScopesResponse> getScope(final int frameId) {
        return this.asyncRequest(new DAPScopesRequest(frameId)).then(resp -> {
            DAPScopesResponse scope = (DAPScopesResponse) resp;
            if (scope != null && scope.body != null && scope.body.scopes != null) {
                for (final DAPScope dapScope : scope.body.scopes) {
                    // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize
                    // request)
                    if (dapScope.line != null) {
                        dapScope.line -= linesStartAt1 ? 1 : 0;
                    }
                }
            }
            return scope;
        });
    }

    public Promise<DAPVariablesResponse> getVariable(int varRef, final int offset, final int maxResults) {
        // TODO fmtk doesnt support paging
        return this.asyncRequest(new DAPVariablesRequest(varRef));
    }

    public Promise<DAPConfigurationDoneResponse> configurationDone() {
        return this.asyncRequest(new DAPConfigurationDoneRequest());
    }

    public Promise<DAPNextResponse> stepOver() {
        return this.asyncRequest(new DAPNextRequest(DAPSteppingGranularity.STATEMENT));
    }

    public Promise<DAPThreadsResponse> getThreads() {
        return this.asyncRequest(new DAPThreadsRequest());
    }

    public String toJSON(final Object obj) {
        if(obj == null) return "null";
        try {
            return this.myDAPSocket.getPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
           return String.format("{ error: \"%s\" }", e.getMessage());
        }
    }

    public /*Promise<DAPStep>*/void stepOut() {
    }

    public Promise<DAPStepInResponse> stepInto(@Nullable Integer targetId) {
        DAPStepInRequest stepInRequest = new DAPStepInRequest();
        if(targetId != null) {
            stepInRequest.getArguments().targetId = targetId;
        }

        return this.asyncRequest(stepInRequest);
    }

    public Promise<DAPBreakpointLocationsResponse> getBreakpointLocations(@NotNull VirtualFile file, int startLine, int endLine) {
        // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
        endLine = endLine < 0 ? startLine : endLine;

        DAPBreakpointLocationsRequest request = new DAPBreakpointLocationsRequest(file, startLine, endLine);
        return this.asyncRequest(request).then(resp -> {
            DAPBreakpointLocationsResponse loc = (DAPBreakpointLocationsResponse) resp;

            if (loc != null && loc.body != null && loc.body.breakpoints != null) {
                for (final DAPBreakpointLocation breakpoint : loc.body.breakpoints) {
                    breakpoint.line -= linesStartAt1 ? 1 : 0;
                }
            }
            return loc;
        });
    }

    public Promise<DAPEvaluateResponse> evaluate(final String expression, final int frameId, final DAPEvaluateRequest.EvalContext context) {
        return this.asyncRequest(new DAPEvaluateRequest(expression, frameId, context));
    }

    public Promise<DAPCompletionsResponse> getCompletions(final String text, final int myFrameId, final int line, final int column) {
        DAPCompletionsRequest completionsRequest;
        if(line == 0) completionsRequest = new DAPCompletionsRequest(myFrameId, text, column);
        else completionsRequest = new DAPCompletionsRequest(myFrameId, text, line, column);

        return this.asyncRequest(completionsRequest);
        //EditorMouseHoverPopupManager.getInstance
        //HintManager
    }

    public Promise<DAPStepInTargetsResponse> getStepIntoTargets(final int frameId) {
        return this.asyncRequest(new DAPStepInTargetsRequest(frameId));
    }

    public Promise<DAPSetExpressionResponse> setExpression(@NotNull final String evaluationExpression,
                                                           @NotNull final String expression,
                                                           @Nullable final Integer frameId) {
        return this.asyncRequest(new DAPSetExpressionRequest(evaluationExpression, expression, frameId));
    }

    public Promise<DAPSetVariableResponse> setValue(final int id, @NotNull final String evaluationExpression,
                                                    @NotNull final String expression) {
        return this.asyncRequest(new DAPSetVariableRequest(id, evaluationExpression, expression));
    }

    public boolean wasTerminationRequested() {
        return this.myDAPSocket.wasTerminationRequested();
    }

    public ProcessListener getSocket() {
        return this.myDAPSocket;
    }

    public String getLastReceivedMessage() {
        return this.myDAPSocket.getLastReceivedMessage();
    }

    public Promise<List<Boolean>> whenPreviousEventsProcessed(int receive_sequence) {
        return this.myDAPSocket.whenPreviousEventsProcessed(receive_sequence);
    }

    public void setTerminating() {
        this.myDAPSocket.setTerminating();
    }

    protected static class BreakpointsInFile {
        private VirtualFile myFile;
        private final List<Pair<Integer, AsyncPromise<DAPSourceBreakpoint>>> positions = new ArrayList<>();

        private final boolean linesStartAt1;

        BreakpointsInFile(boolean linesStartAt1) {
            this.linesStartAt1 = linesStartAt1;
        }

        public Promise<DAPSourceBreakpoint> add(@NotNull XSourcePosition position) {
            if(myFile == null) {
                myFile = position.getFile();
            }

            if(!Objects.equals(myFile.getUrl(), position.getFile().getUrl())) {
                return Promises.rejectedPromise(
                    String.format("Cannot add breakpoint of file '%s' to this file '%s'",
                        position.getFile().getPath(),
                        myFile.getPath()));
            }

            AsyncPromise<DAPSourceBreakpoint> prom = new AsyncPromise<>();
            positions.add(new Pair<>(position.getLine(), prom));
            return prom;
        }

        public int addGetIndex(@NotNull XSourcePosition position) {
            if(myFile == null) {
                myFile = position.getFile();
            }

            if(!Objects.equals(myFile.getUrl(), position.getFile().getUrl())) {
                return -1;
            }

            positions.add(new Pair<>(position.getLine(), null));
            return positions.size() -1;
        }

        public void remove(@NotNull XSourcePosition position) {
            int lineToFind = position.getLine();
            int indx = -1;
            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).first == lineToFind) {
                    indx = i;
                    break;
                }
            }
            if(indx == -1) return;

            AsyncPromise<DAPSourceBreakpoint> prom = positions.remove(indx).second;
            if(prom != null) prom.cancel();
        }

        public DAPSetBreakpointsRequest getSetBreakpointRequest() {
            if(myFile != null) {
                // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
                List<Integer> linePositions = positions.stream().map(pair -> pair.first + (linesStartAt1 ? 1 : 0)).collect(Collectors.toList());
                return new DAPSetBreakpointsRequest(myFile, linePositions);
            }
            return null;
        }

        public void handleSetBreakpointResponse(@Nullable DAPSetBreakpointsResponse resp) {
            if(resp != null && resp.body != null) {
                for (int i = 0; i < resp.body.breakpoints.length; i++) {
                    DAPSourceBreakpoint breakpoint = resp.body.breakpoints[i];
                    // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
                    breakpoint.line -= linesStartAt1 ? 1 : 0;

                    AsyncPromise<DAPSourceBreakpoint> prom = findPromiseByBreakpoint(breakpoint);
                    if(prom != null) prom.setResult(breakpoint);
                }
            }
        }

        private AsyncPromise<DAPSourceBreakpoint> findPromiseByBreakpoint(@NotNull DAPSourceBreakpoint breakpoint) {
            for (final Pair<Integer, AsyncPromise<DAPSourceBreakpoint>> position : positions) {
                if(position.first == breakpoint.line) {
                    return position.second;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            StringBuilder lines = new StringBuilder();

            for (final Pair<Integer, AsyncPromise<DAPSourceBreakpoint>> position : this.positions) {
                lines.append(position.first);
                lines.append(",");
            }

            return String.format("'%s' lines: [%s]", this.myFile.getUrl(), lines.substring(0,lines.length()-1));
        }
    }
}
