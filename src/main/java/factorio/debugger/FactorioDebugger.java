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
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.DAPEventNames;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.requests.DAPBreakpointLocationsRequest;
import factorio.debugger.DAP.messages.requests.DAPCompletionsRequest;
import factorio.debugger.DAP.messages.requests.DAPConfigurationDoneRequest;
import factorio.debugger.DAP.messages.requests.DAPContinueRequest;
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest;
import factorio.debugger.DAP.messages.requests.DAPInitializeRequest;
import factorio.debugger.DAP.messages.requests.DAPLaunchRequest;
import factorio.debugger.DAP.messages.requests.DAPNextRequest;
import factorio.debugger.DAP.messages.requests.DAPPauseRequest;
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
import factorio.debugger.DAP.messages.response.DAPBreakpointLocationsResponse;
import factorio.debugger.DAP.messages.response.DAPCompletionsResponse;
import factorio.debugger.DAP.messages.response.DAPConfigurationDoneResponse;
import factorio.debugger.DAP.messages.response.DAPEvaluateResponse;
import factorio.debugger.DAP.messages.response.DAPInitializeResponse;
import factorio.debugger.DAP.messages.response.DAPNextResponse;
import factorio.debugger.DAP.messages.response.DAPScopesResponse;
import factorio.debugger.DAP.messages.response.DAPSetBreakpointsResponse;
import factorio.debugger.DAP.messages.response.DAPSetExpressionResponse;
import factorio.debugger.DAP.messages.response.DAPSetVariableResponse;
import factorio.debugger.DAP.messages.response.DAPStackTraceResponse;
import factorio.debugger.DAP.messages.response.DAPStepInResponse;
import factorio.debugger.DAP.messages.response.DAPStepInTargetsResponse;
import factorio.debugger.DAP.messages.response.DAPTerminateResponse;
import factorio.debugger.DAP.messages.response.DAPThreadsResponse;
import factorio.debugger.DAP.messages.response.DAPVariablesResponse;
import factorio.debugger.DAP.messages.types.DAPCapabilities;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint;
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity;

public class FactorioDebugger {
    private Logger logger = Logger.getInstance(FactorioDebugger.class);
    private final FactorioDebugProcess myFactorioDebugProcess;
    private final DAPSocket myDAPSocket;

    private DAPInitializeResponse initializeResponse;
    private boolean initialized;

    private CircularFifoBuffer lastMessages;

    private Map<String, BreakpointsInFile> breakpointsPerFile;
    private boolean batchBreakpointRequests;

    public FactorioDebugger(final FactorioDebugProcess factorioDebugProcess) {
        myFactorioDebugProcess = factorioDebugProcess;
        initialized = false;
        myDAPSocket = new DAPSocket(myFactorioDebugProcess.getProcessHandler().getProcessInput());

        breakpointsPerFile = new HashMap<>();
        batchBreakpointRequests = true;

        lastMessages = new CircularFifoBuffer(20);

        DAPInitializeRequest initializeRequest = new DAPInitializeRequest();

        ApplicationNamesInfo appinfo = ApplicationNamesInfo.getInstance();
        initializeRequest.arguments.clientID = appinfo.getScriptName();
        initializeRequest.arguments.clientName = appinfo.getFullProductName();
        initializeRequest.arguments.adapterID = "factorio-debugger-intellij";

        initializeRequest.arguments.linesStartAt1 = true;
        initializeRequest.arguments.columnsStartAt1 = true;
        initializeRequest.arguments.supportsMemoryEvent = true;
        initializeRequest.arguments.supportsVariableType = true;
        initializeRequest.arguments.supportsVariablePaging = true;
        initializeRequest.arguments.supportsInvalidatedEvent = true;
        initializeRequest.arguments.supportsMemoryReferences = true;
        initializeRequest.arguments.supportsProgressReporting = true;
        initializeRequest.arguments.supportsRunInTerminalRequest = true;

        initializeRequest.arguments.locale = Locale.getDefault().toString().replaceAll("_", "-");
        initializeRequest.arguments.pathFormat = "path"; // fmtk only supports "paths"

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
        launchRequest.arguments.setOtherField("type", "factoriomod");
        launchRequest.arguments.setOtherField("request", "launch");
        launchRequest.arguments.setOtherField("trace", true);
        myDAPSocket.sendRequest(launchRequest);
    }

    public @NotNull DAPCapabilities getCapabilities() {
        return this.initializeResponse != null ? this.initializeResponse.body : DAPCapabilities.EMPTY_CAPABILITES;
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
            this.breakpointsPerFile.put(filePath, new BreakpointsInFile());
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
            this.breakpointsPerFile.put(filePath, new BreakpointsInFile());
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
        stackTraceRequest.arguments.levels = Integer.MAX_VALUE;
        return asyncRequest(stackTraceRequest);
    }

    public Promise<DAPScopesResponse> getScope(final int frameId) {
        return this.asyncRequest(new DAPScopesRequest(frameId));
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
            stepInRequest.arguments.targetId = targetId;
        }

        return this.asyncRequest(stepInRequest);
    }

    public Promise<DAPBreakpointLocationsResponse> getBreakpointLocations() {
        DAPBreakpointLocationsRequest request = new DAPBreakpointLocationsRequest();
        request.arguments.source.path = "/DataSSD/Games/factorio_80/mods/factorio-codex/build/Migration.lua";
        request.arguments.line = 1;
        request.arguments.endLine = 90;
        return this.asyncRequest(request);
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
            int indx = positions.indexOf(position.getLine());
            if(indx == -1 || positions.size() == 0) return;

            AsyncPromise<DAPSourceBreakpoint> prom = positions.remove(indx).second;
            if(prom != null) prom.cancel();
        }

        public DAPSetBreakpointsRequest getSetBreakpointRequest() {
            if(myFile != null) {
                List<Integer> linePositions = positions.stream().map(pair -> pair.first).collect(Collectors.toList());
                return new DAPSetBreakpointsRequest(myFile, linePositions);
            }
            return null;
        }

        public void handleSetBreakpointResponse(@Nullable DAPSetBreakpointsResponse resp) {
            if(resp != null && resp.body != null) {
                for (int i = 0; i < resp.body.breakpoints.length; i++) {
                    DAPSourceBreakpoint breakpoint = resp.body.breakpoints[i];

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
