package factorio.debugger.DAP.messages.types;

import java.util.Collection;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPCapabilities extends DAPAdditionalProperties {
    public final static DAPCapabilities EMPTY_CAPABILITES = new DAPCapabilities();

    public boolean has(DAPCapabilitiesEnum capability) {
        return switch (capability) {
            case ConfigurationDoneRequest -> supportsConfigurationDoneRequest;
            case FunctionBreakpoints -> supportsFunctionBreakpoints;
            case ConditionalBreakpoints -> supportsConditionalBreakpoints;
            case HitConditionalBreakpoints -> supportsHitConditionalBreakpoints;
            case EvaluateForHovers -> supportsEvaluateForHovers;
            case StepBack -> supportsStepBack;
            case SetVariable -> supportsSetVariable;
            case RestartFrame -> supportsRestartFrame;
            case GotoTargetsRequest -> supportsGotoTargetsRequest;
            case StepInTargetsRequest -> supportsStepInTargetsRequest;
            case CompletionsRequest -> supportsCompletionsRequest;
            case ModulesRequest -> supportsModulesRequest;
            case RestartRequest -> supportsRestartRequest;
            case ExceptionOptions -> supportsExceptionOptions;
            case ValueFormattingOptions -> supportsValueFormattingOptions;
            case ExceptionInfoRequest -> supportsExceptionInfoRequest;
            case TerminateDebuggee -> supportTerminateDebuggee;
            case SuspendDebuggee -> supportSuspendDebuggee;
            case DelayedStackTraceLoading -> supportsDelayedStackTraceLoading;
            case LoadedSourcesRequest -> supportsLoadedSourcesRequest;
            case LogPoints -> supportsLogPoints;
            case TerminateThreadsRequest -> supportsTerminateThreadsRequest;
            case SetExpression -> supportsSetExpression;
            case TerminateRequest -> supportsTerminateRequest;
            case DataBreakpoints -> supportsDataBreakpoints;
            case ReadMemoryRequest -> supportsReadMemoryRequest;
            case WriteMemoryRequest -> supportsWriteMemoryRequest;
            case DisassembleRequest -> supportsDisassembleRequest;
            case CancelRequest -> supportsCancelRequest;
            case BreakpointLocationsRequest -> supportsBreakpointLocationsRequest;
            case ClipboardContext -> supportsClipboardContext;
            case SteppingGranularity -> supportsSteppingGranularity;
            case InstructionBreakpoints -> supportsInstructionBreakpoints;
            case ExceptionFilterOptions -> supportsExceptionFilterOptions;
            case SingleThreadExecutionRequests -> supportsSingleThreadExecutionRequests;
        };
    }

    /**
     * The debug adapter supports the `configurationDone` request.
     */
    @JsonProperty("supportsConfigurationDoneRequest")
    public boolean supportsConfigurationDoneRequest;

    /**
     * The debug adapter supports function breakpoints.
     */
    @JsonProperty("supportsFunctionBreakpoints")
    public boolean supportsFunctionBreakpoints;

    /**
     * The debug adapter supports conditional breakpoints.
     */
    @JsonProperty("supportsConditionalBreakpoints")
    public boolean supportsConditionalBreakpoints;

    /**
     * The debug adapter supports breakpoints that break execution after a
     * specified number of hits.
     */
    @JsonProperty("supportsHitConditionalBreakpoints")
    public boolean supportsHitConditionalBreakpoints;

    /**
     * The debug adapter supports a (side effect free) `evaluate` request for data
     * hovers.
     */
    @JsonProperty("supportsEvaluateForHovers")
    public boolean supportsEvaluateForHovers;

    /**
     * Available exception filter options for the `setExceptionBreakpoints`
     * request.
     */
    @JsonProperty("exceptionBreakpointFilters")
    public DAPExceptionBreakpointsFilter[] exceptionBreakpointFilters;

    /**
     * The debug adapter supports stepping back via the `stepBack` and
     * `reverseContinue` requests.
     */
    @JsonProperty("supportsStepBack")
    public boolean supportsStepBack;

    /**
     * The debug adapter supports setting a variable to a value.
     */
    @JsonProperty("supportsSetVariable")
    public boolean supportsSetVariable;

    /**
     * The debug adapter supports restarting a frame.
     */
    @JsonProperty("supportsRestartFrame")
    public boolean supportsRestartFrame;

    /**
     * The debug adapter supports the `gotoTargets` request.
     */
    @JsonProperty("supportsGotoTargetsRequest")
    public boolean supportsGotoTargetsRequest;

    /**
     * The debug adapter supports the `stepInTargets` request.
     */
    @JsonProperty("supportsStepInTargetsRequest")
    public boolean supportsStepInTargetsRequest;

    /**
     * The debug adapter supports the `completions` request.
     */
    @JsonProperty("supportsCompletionsRequest")
    public boolean supportsCompletionsRequest;

    /**
     * The set of characters that should trigger completion in a REPL. If not
     * specified, the UI should assume the `.` character.
     */
    @JsonProperty("completionTriggerCharacters")
    public String[] completionTriggerCharacters;

    /**
     * The debug adapter supports the `modules` request.
     */
    @JsonProperty("supportsModulesRequest")
    public boolean supportsModulesRequest;

    /**
     * The set of additional module information exposed by the debug adapter.
     */
    @JsonProperty("additionalModuleColumns")
    public DAPColumnDescriptor[] additionalModuleColumns;

    /**
     * Checksum algorithms supported by the debug adapter.
     */
    @JsonProperty("supportedChecksumAlgorithms")
    public DAPChecksum.ChecksumAlgorithm[] supportedChecksumAlgorithms;

    /**
     * The debug adapter supports the `restart` request. In this case a client
     * should not implement `restart` by terminating and relaunching the adapter
     * but by calling the `restart` request.
     */
    @JsonProperty("supportsRestartRequest")
    public boolean supportsRestartRequest;

    /**
     * The debug adapter supports `exceptionOptions` on the
     * `setExceptionBreakpoints` request.
     */
    @JsonProperty("supportsExceptionOptions")
    public boolean supportsExceptionOptions;

    /**
     * The debug adapter supports a `format` attribute on the `stackTrace`,
     * `variables`, and `evaluate` requests.
     */
    @JsonProperty("supportsValueFormattingOptions")
    public boolean supportsValueFormattingOptions;

    /**
     * The debug adapter supports the `exceptionInfo` request.
     */
    @JsonProperty("supportsExceptionInfoRequest")
    public boolean supportsExceptionInfoRequest;

    /**
     * The debug adapter supports the `terminateDebuggee` attribute on the
     * `disconnect` request.
     */
    @JsonProperty("supportTerminateDebuggee")
    public boolean supportTerminateDebuggee;

    /**
     * The debug adapter supports the `suspendDebuggee` attribute on the
     * `disconnect` request.
     */
    @JsonProperty("supportSuspendDebuggee")
    public boolean supportSuspendDebuggee;

    /**
     * The debug adapter supports the delayed loading of parts of the stack, which
     * requires that both the `startFrame` and `levels` arguments and the
     * `totalFrames` result of the `stackTrace` request are supported.
     */
    @JsonProperty("supportsDelayedStackTraceLoading")
    public boolean supportsDelayedStackTraceLoading;

    /**
     * The debug adapter supports the `loadedSources` request.
     */
    @JsonProperty("supportsLoadedSourcesRequest")
    public boolean supportsLoadedSourcesRequest;

    /**
     * The debug adapter supports log points by interpreting the `logMessage`
     * attribute of the `SourceBreakpoint`.
     */
    @JsonProperty("supportsLogPoints")
    public boolean supportsLogPoints;

    /**
     * The debug adapter supports the `terminateThreads` request.
     */
    @JsonProperty("supportsTerminateThreadsRequest")
    public boolean supportsTerminateThreadsRequest;

    /**
     * The debug adapter supports the `setExpression` request.
     */
    @JsonProperty("supportsSetExpression")
    public boolean supportsSetExpression;

    /**
     * The debug adapter supports the `terminate` request.
     */
    @JsonProperty("supportsTerminateRequest")
    public boolean supportsTerminateRequest;

    /**
     * The debug adapter supports data breakpoints.
     */
    @JsonProperty("supportsDataBreakpoints")
    public boolean supportsDataBreakpoints;

    /**
     * The debug adapter supports the `readMemory` request.
     */
    @JsonProperty("supportsReadMemoryRequest")
    public boolean supportsReadMemoryRequest;

    /**
     * The debug adapter supports the `writeMemory` request.
     */
    @JsonProperty("supportsWriteMemoryRequest")
    public boolean supportsWriteMemoryRequest;

    /**
     * The debug adapter supports the `disassemble` request.
     */
    @JsonProperty("supportsDisassembleRequest")
    public boolean supportsDisassembleRequest;

    /**
     * The debug adapter supports the `cancel` request.
     */
    @JsonProperty("supportsCancelRequest")
    public boolean supportsCancelRequest;

    /**
     * The debug adapter supports the `breakpointLocations` request.
     */
    @JsonProperty("supportsBreakpointLocationsRequest")
    public boolean supportsBreakpointLocationsRequest;

    /**
     * The debug adapter supports the `clipboard` context value in the `evaluate`
     * request.
     */
    @JsonProperty("supportsClipboardContext")
    public boolean supportsClipboardContext;

    /**
     * The debug adapter supports stepping granularities (argument `granularity`)
     * for the stepping requests.
     */
    @JsonProperty("supportsSteppingGranularity")
    public boolean supportsSteppingGranularity;

    /**
     * The debug adapter supports adding breakpoints based on instruction
     * references.
     */
    @JsonProperty("supportsInstructionBreakpoints")
    public boolean supportsInstructionBreakpoints;

    /**
     * The debug adapter supports `filterOptions` as an argument on the
     * `setExceptionBreakpoints` request.
     */
    @JsonProperty("supportsExceptionFilterOptions")
    public boolean supportsExceptionFilterOptions;

    /**
     * The debug adapter supports the `singleThread` property on the execution
     * requests (`continue`, `next`, `stepIn`, `stepOut`, `reverseContinue`,
     * `stepBack`).
     */
    @JsonProperty("supportsSingleThreadExecutionRequests")
    public boolean supportsSingleThreadExecutionRequests;

    public DAPCapabilities() {
        this.exceptionBreakpointFilters = new DAPExceptionBreakpointsFilter[0];
        this.completionTriggerCharacters = new String[0];
        this.additionalModuleColumns = new DAPColumnDescriptor[0];
        this.supportedChecksumAlgorithms = new DAPChecksum.ChecksumAlgorithm[0];

    }

    public Collection<DAPCapabilitiesEnum> getCapabilities() {
        Collection<DAPCapabilitiesEnum> result = new HashSet<>();
        for (final DAPCapabilitiesEnum value : DAPCapabilitiesEnum.values()) {
            if(this.has(value)) result.add(value);
        }
        return result;
    }
}
