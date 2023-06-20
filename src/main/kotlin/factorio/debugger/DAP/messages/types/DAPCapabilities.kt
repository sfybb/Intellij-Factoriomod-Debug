package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPCapabilities : DAPAdditionalProperties() {
    fun has(capability: DAPCapabilitiesEnum): Boolean {
        return when (capability) {
            DAPCapabilitiesEnum.ConfigurationDoneRequest -> supportsConfigurationDoneRequest
            DAPCapabilitiesEnum.FunctionBreakpoints -> supportsFunctionBreakpoints
            DAPCapabilitiesEnum.ConditionalBreakpoints -> supportsConditionalBreakpoints
            DAPCapabilitiesEnum.HitConditionalBreakpoints -> supportsHitConditionalBreakpoints
            DAPCapabilitiesEnum.EvaluateForHovers -> supportsEvaluateForHovers
            DAPCapabilitiesEnum.StepBack -> supportsStepBack
            DAPCapabilitiesEnum.SetVariable -> supportsSetVariable
            DAPCapabilitiesEnum.RestartFrame -> supportsRestartFrame
            DAPCapabilitiesEnum.GotoTargetsRequest -> supportsGotoTargetsRequest
            DAPCapabilitiesEnum.StepInTargetsRequest -> supportsStepInTargetsRequest
            DAPCapabilitiesEnum.CompletionsRequest -> supportsCompletionsRequest
            DAPCapabilitiesEnum.ModulesRequest -> supportsModulesRequest
            DAPCapabilitiesEnum.RestartRequest -> supportsRestartRequest
            DAPCapabilitiesEnum.ExceptionOptions -> supportsExceptionOptions
            DAPCapabilitiesEnum.ValueFormattingOptions -> supportsValueFormattingOptions
            DAPCapabilitiesEnum.ExceptionInfoRequest -> supportsExceptionInfoRequest
            DAPCapabilitiesEnum.TerminateDebuggee -> supportTerminateDebuggee
            DAPCapabilitiesEnum.SuspendDebuggee -> supportSuspendDebuggee
            DAPCapabilitiesEnum.DelayedStackTraceLoading -> supportsDelayedStackTraceLoading
            DAPCapabilitiesEnum.LoadedSourcesRequest -> supportsLoadedSourcesRequest
            DAPCapabilitiesEnum.LogPoints -> supportsLogPoints
            DAPCapabilitiesEnum.TerminateThreadsRequest -> supportsTerminateThreadsRequest
            DAPCapabilitiesEnum.SetExpression -> supportsSetExpression
            DAPCapabilitiesEnum.TerminateRequest -> supportsTerminateRequest
            DAPCapabilitiesEnum.DataBreakpoints -> supportsDataBreakpoints
            DAPCapabilitiesEnum.ReadMemoryRequest -> supportsReadMemoryRequest
            DAPCapabilitiesEnum.WriteMemoryRequest -> supportsWriteMemoryRequest
            DAPCapabilitiesEnum.DisassembleRequest -> supportsDisassembleRequest
            DAPCapabilitiesEnum.CancelRequest -> supportsCancelRequest
            DAPCapabilitiesEnum.BreakpointLocationsRequest -> supportsBreakpointLocationsRequest
            DAPCapabilitiesEnum.ClipboardContext -> supportsClipboardContext
            DAPCapabilitiesEnum.SteppingGranularity -> supportsSteppingGranularity
            DAPCapabilitiesEnum.InstructionBreakpoints -> supportsInstructionBreakpoints
            DAPCapabilitiesEnum.ExceptionFilterOptions -> supportsExceptionFilterOptions
            DAPCapabilitiesEnum.SingleThreadExecutionRequests -> supportsSingleThreadExecutionRequests
        }
    }

    /**
     * The debug adapter supports the `configurationDone` request.
     */
    @JsonProperty("supportsConfigurationDoneRequest")
    var supportsConfigurationDoneRequest = false

    /**
     * The debug adapter supports function breakpoints.
     */
    @JsonProperty("supportsFunctionBreakpoints")
    var supportsFunctionBreakpoints = false

    /**
     * The debug adapter supports conditional breakpoints.
     */
    @JsonProperty("supportsConditionalBreakpoints")
    var supportsConditionalBreakpoints = false

    /**
     * The debug adapter supports breakpoints that break execution after a
     * specified number of hits.
     */
    @JsonProperty("supportsHitConditionalBreakpoints")
    var supportsHitConditionalBreakpoints = false

    /**
     * The debug adapter supports a (side effect free) `evaluate` request for data
     * hovers.
     */
    @JsonProperty("supportsEvaluateForHovers")
    var supportsEvaluateForHovers = false

    /**
     * Available exception filter options for the `setExceptionBreakpoints`
     * request.
     */
    @JsonProperty("exceptionBreakpointFilters")
    var exceptionBreakpointFilters: Array<DAPExceptionBreakpointsFilter> = arrayOf()

    /**
     * The debug adapter supports stepping back via the `stepBack` and
     * `reverseContinue` requests.
     */
    @JsonProperty("supportsStepBack")
    var supportsStepBack = false

    /**
     * The debug adapter supports setting a variable to a value.
     */
    @JsonProperty("supportsSetVariable")
    var supportsSetVariable = false

    /**
     * The debug adapter supports restarting a frame.
     */
    @JsonProperty("supportsRestartFrame")
    var supportsRestartFrame = false

    /**
     * The debug adapter supports the `gotoTargets` request.
     */
    @JsonProperty("supportsGotoTargetsRequest")
    var supportsGotoTargetsRequest = false

    /**
     * The debug adapter supports the `stepInTargets` request.
     */
    @JsonProperty("supportsStepInTargetsRequest")
    var supportsStepInTargetsRequest = false

    /**
     * The debug adapter supports the `completions` request.
     */
    @JsonProperty("supportsCompletionsRequest")
    var supportsCompletionsRequest = false

    /**
     * The set of characters that should trigger completion in a REPL. If not
     * specified, the UI should assume the `.` character.
     */
    @JsonProperty("completionTriggerCharacters")
    var completionTriggerCharacters: Array<String> = arrayOf(".")

    /**
     * The debug adapter supports the `modules` request.
     */
    @JsonProperty("supportsModulesRequest")
    var supportsModulesRequest = false

    /**
     * The set of additional module information exposed by the debug adapter.
     */
    @JsonProperty("additionalModuleColumns")
    var additionalModuleColumns: Array<DAPColumnDescriptor> = arrayOf()

    /**
     * Checksum algorithms supported by the debug adapter.
     */
    @JsonProperty("supportedChecksumAlgorithms")
    var supportedChecksumAlgorithms: Array<DAPChecksum.ChecksumAlgorithm> = arrayOf()

    /**
     * The debug adapter supports the `restart` request. In this case a client
     * should not implement `restart` by terminating and relaunching the adapter
     * but by calling the `restart` request.
     */
    @JsonProperty("supportsRestartRequest")
    var supportsRestartRequest = false

    /**
     * The debug adapter supports `exceptionOptions` on the
     * `setExceptionBreakpoints` request.
     */
    @JsonProperty("supportsExceptionOptions")
    var supportsExceptionOptions = false

    /**
     * The debug adapter supports a `format` attribute on the `stackTrace`,
     * `variables`, and `evaluate` requests.
     */
    @JsonProperty("supportsValueFormattingOptions")
    var supportsValueFormattingOptions = false

    /**
     * The debug adapter supports the `exceptionInfo` request.
     */
    @JsonProperty("supportsExceptionInfoRequest")
    var supportsExceptionInfoRequest = false

    /**
     * The debug adapter supports the `terminateDebuggee` attribute on the
     * `disconnect` request.
     */
    @JsonProperty("supportTerminateDebuggee")
    var supportTerminateDebuggee = false

    /**
     * The debug adapter supports the `suspendDebuggee` attribute on the
     * `disconnect` request.
     */
    @JsonProperty("supportSuspendDebuggee")
    var supportSuspendDebuggee = false

    /**
     * The debug adapter supports the delayed loading of parts of the stack, which
     * requires that both the `startFrame` and `levels` arguments and the
     * `totalFrames` result of the `stackTrace` request are supported.
     */
    @JsonProperty("supportsDelayedStackTraceLoading")
    var supportsDelayedStackTraceLoading = false

    /**
     * The debug adapter supports the `loadedSources` request.
     */
    @JsonProperty("supportsLoadedSourcesRequest")
    var supportsLoadedSourcesRequest = false

    /**
     * The debug adapter supports log points by interpreting the `logMessage`
     * attribute of the `SourceBreakpoint`.
     */
    @JsonProperty("supportsLogPoints")
    var supportsLogPoints = false

    /**
     * The debug adapter supports the `terminateThreads` request.
     */
    @JsonProperty("supportsTerminateThreadsRequest")
    var supportsTerminateThreadsRequest = false

    /**
     * The debug adapter supports the `setExpression` request.
     */
    @JsonProperty("supportsSetExpression")
    var supportsSetExpression = false

    /**
     * The debug adapter supports the `terminate` request.
     */
    @JsonProperty("supportsTerminateRequest")
    var supportsTerminateRequest = false

    /**
     * The debug adapter supports data breakpoints.
     */
    @JsonProperty("supportsDataBreakpoints")
    var supportsDataBreakpoints = false

    /**
     * The debug adapter supports the `readMemory` request.
     */
    @JsonProperty("supportsReadMemoryRequest")
    var supportsReadMemoryRequest = false

    /**
     * The debug adapter supports the `writeMemory` request.
     */
    @JsonProperty("supportsWriteMemoryRequest")
    var supportsWriteMemoryRequest = false

    /**
     * The debug adapter supports the `disassemble` request.
     */
    @JsonProperty("supportsDisassembleRequest")
    var supportsDisassembleRequest = false

    /**
     * The debug adapter supports the `cancel` request.
     */
    @JsonProperty("supportsCancelRequest")
    var supportsCancelRequest = false

    /**
     * The debug adapter supports the `breakpointLocations` request.
     */
    @JsonProperty("supportsBreakpointLocationsRequest")
    var supportsBreakpointLocationsRequest = false

    /**
     * The debug adapter supports the `clipboard` context value in the `evaluate`
     * request.
     */
    @JsonProperty("supportsClipboardContext")
    var supportsClipboardContext = false

    /**
     * The debug adapter supports stepping granularities (argument `granularity`)
     * for the stepping requests.
     */
    @JsonProperty("supportsSteppingGranularity")
    var supportsSteppingGranularity = false

    /**
     * The debug adapter supports adding breakpoints based on instruction
     * references.
     */
    @JsonProperty("supportsInstructionBreakpoints")
    var supportsInstructionBreakpoints = false

    /**
     * The debug adapter supports `filterOptions` as an argument on the
     * `setExceptionBreakpoints` request.
     */
    @JsonProperty("supportsExceptionFilterOptions")
    var supportsExceptionFilterOptions = false

    /**
     * The debug adapter supports the `singleThread` property on the execution
     * requests (`continue`, `next`, `stepIn`, `stepOut`, `reverseContinue`,
     * `stepBack`).
     */
    @JsonProperty("supportsSingleThreadExecutionRequests")
    var supportsSingleThreadExecutionRequests = false

    val capabilities: Collection<DAPCapabilitiesEnum>
        get() {
            val result: MutableCollection<DAPCapabilitiesEnum> = HashSet()
            for (value in DAPCapabilitiesEnum.values()) {
                if (has(value)) result.add(value)
            }
            return result
        }

    companion object {
        @JvmField
        val EMPTY_CAPABILITIES = DAPCapabilities()
    }
}
