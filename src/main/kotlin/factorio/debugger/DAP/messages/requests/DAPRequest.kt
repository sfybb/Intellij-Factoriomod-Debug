package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import factorio.debugger.DAP.messages.DAPProtocolMessage
import factorio.debugger.DAP.messages.requests.*

/**
 * The command to execute.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "command")
@JsonSubTypes(
    JsonSubTypes.Type(value = DAPAttachRequest::class),
    JsonSubTypes.Type(value = DAPBreakpointLocationsRequest::class),
    JsonSubTypes.Type(value = DAPCompletionsRequest::class),
    JsonSubTypes.Type(value = DAPCancelRequest::class),
    JsonSubTypes.Type(value = DAPConfigurationDoneRequest::class),
    JsonSubTypes.Type(value = DAPContinueRequest::class),
    JsonSubTypes.Type(value = DAPDataBreakpointInfoRequest::class),
    JsonSubTypes.Type(value = DAPDisassembleRequest::class),
    JsonSubTypes.Type(value = DAPDisconnectRequest::class),
    JsonSubTypes.Type(value = DAPEvaluateRequest::class),
    JsonSubTypes.Type(value = DAPExceptionInfoRequest::class),
    JsonSubTypes.Type(value = DAPInitializeRequest::class),
    JsonSubTypes.Type(value = DAPLaunchRequest::class),
    JsonSubTypes.Type(value = DAPNextRequest::class),
    JsonSubTypes.Type(value = DAPPauseRequest::class),
    JsonSubTypes.Type(value = DAPRestartRequest::class),
    JsonSubTypes.Type(value = DAPScopesRequest::class),
    JsonSubTypes.Type(value = DAPSetBreakpointsRequest::class),
    JsonSubTypes.Type(value = DAPSetExpressionRequest::class),
    JsonSubTypes.Type(value = DAPSetVariableRequest::class),
    JsonSubTypes.Type(value = DAPStepInRequest::class),
    JsonSubTypes.Type(value = DAPStepInTargetsRequest::class),
    JsonSubTypes.Type(value = DAPTerminateRequest::class),
    JsonSubTypes.Type(value = DAPThreadsRequest::class),
    JsonSubTypes.Type(value = DAPVariablesRequest::class)
) // extends DAPRequest
@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class DAPRequest<D> (
    /**
     * Object containing arguments for the command.
     */
    @JsonProperty("arguments")
    var arguments: D
) : DAPProtocolMessage() {
    init {
        type = "request"
    }
}
