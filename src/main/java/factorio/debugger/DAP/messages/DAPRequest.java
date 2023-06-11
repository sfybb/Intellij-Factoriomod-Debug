package factorio.debugger.DAP.messages;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import factorio.debugger.DAP.messages.requests.DAPAttachRequest;
import factorio.debugger.DAP.messages.requests.DAPBreakpointLocationsRequest;
import factorio.debugger.DAP.messages.requests.DAPCancelRequest;
import factorio.debugger.DAP.messages.requests.DAPCompletionsRequest;
import factorio.debugger.DAP.messages.requests.DAPConfigurationDoneRequest;
import factorio.debugger.DAP.messages.requests.DAPContinueRequest;
import factorio.debugger.DAP.messages.requests.DAPDataBreakpointInfoRequest;
import factorio.debugger.DAP.messages.requests.DAPDisassembleRequest;
import factorio.debugger.DAP.messages.requests.DAPDisconnectRequest;
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest;
import factorio.debugger.DAP.messages.requests.DAPExceptionInfoRequest;
import factorio.debugger.DAP.messages.requests.DAPInitializeRequest;
import factorio.debugger.DAP.messages.requests.DAPLaunchRequest;
import factorio.debugger.DAP.messages.requests.DAPNextRequest;
import factorio.debugger.DAP.messages.requests.DAPPauseRequest;
import factorio.debugger.DAP.messages.requests.DAPRestartRequest;
import factorio.debugger.DAP.messages.requests.DAPScopesRequest;
import factorio.debugger.DAP.messages.requests.DAPSetBreakpointsRequest;
import factorio.debugger.DAP.messages.requests.DAPSetExpressionRequest;
import factorio.debugger.DAP.messages.requests.DAPSetVariableRequest;
import factorio.debugger.DAP.messages.requests.DAPStepInRequest;
import factorio.debugger.DAP.messages.requests.DAPStepInTargetsRequest;
import factorio.debugger.DAP.messages.requests.DAPTerminateRequest;
import factorio.debugger.DAP.messages.requests.DAPThreadsRequest;
import factorio.debugger.DAP.messages.requests.DAPVariablesRequest;

/**
 * The command to execute.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "command"
    //visible = true*/
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DAPAttachRequest.class),
    @JsonSubTypes.Type(value = DAPBreakpointLocationsRequest.class),
    @JsonSubTypes.Type(value = DAPCompletionsRequest.class),
    @JsonSubTypes.Type(value = DAPCancelRequest.class),
    @JsonSubTypes.Type(value = DAPConfigurationDoneRequest.class),
    @JsonSubTypes.Type(value = DAPContinueRequest.class),
    @JsonSubTypes.Type(value = DAPDataBreakpointInfoRequest.class),
    @JsonSubTypes.Type(value = DAPDisassembleRequest.class),
    @JsonSubTypes.Type(value = DAPDisconnectRequest.class),
    @JsonSubTypes.Type(value = DAPEvaluateRequest.class),
    @JsonSubTypes.Type(value = DAPExceptionInfoRequest.class),
    @JsonSubTypes.Type(value = DAPInitializeRequest.class),
    @JsonSubTypes.Type(value = DAPLaunchRequest.class),
    @JsonSubTypes.Type(value = DAPNextRequest.class),
    @JsonSubTypes.Type(value = DAPPauseRequest.class),
    @JsonSubTypes.Type(value = DAPRestartRequest.class),
    @JsonSubTypes.Type(value = DAPScopesRequest.class),
    @JsonSubTypes.Type(value = DAPSetBreakpointsRequest.class),
    @JsonSubTypes.Type(value = DAPSetExpressionRequest.class),
    @JsonSubTypes.Type(value = DAPSetVariableRequest.class),
    @JsonSubTypes.Type(value = DAPStepInRequest.class),
    @JsonSubTypes.Type(value = DAPStepInTargetsRequest.class),
    @JsonSubTypes.Type(value = DAPTerminateRequest.class),
    @JsonSubTypes.Type(value = DAPThreadsRequest.class),
    @JsonSubTypes.Type(value = DAPVariablesRequest.class),
})// extends DAPRequest
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DAPRequest<D> extends DAPProtocolMessage {
    public DAPRequest() {
        type = "request";
    }

    /**
     * Object containing arguments for the command.
     */
    @JsonProperty("arguments")
    public D arguments;
}


