package factorio.debugger.DAP.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import factorio.debugger.DAP.messages.response.DAPBreakpointLocationsResponse;
import factorio.debugger.DAP.messages.response.DAPCancelResponse;
import factorio.debugger.DAP.messages.response.DAPCompletionsResponse;
import factorio.debugger.DAP.messages.response.DAPConfigurationDoneResponse;
import factorio.debugger.DAP.messages.response.DAPContinueResponse;
import factorio.debugger.DAP.messages.response.DAPDataBreakpointInfoResponse;
import factorio.debugger.DAP.messages.response.DAPDisassembleResponse;
import factorio.debugger.DAP.messages.response.DAPEvaluateResponse;
import factorio.debugger.DAP.messages.response.DAPExceptionInfoResponse;
import factorio.debugger.DAP.messages.response.DAPInitializeResponse;
import factorio.debugger.DAP.messages.response.DAPLaunchResponse;
import factorio.debugger.DAP.messages.response.DAPNextResponse;
import factorio.debugger.DAP.messages.response.DAPPauseResponse;
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

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "command",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DAPBreakpointLocationsResponse.class),
    @JsonSubTypes.Type(value = DAPCancelResponse.class),
    @JsonSubTypes.Type(value = DAPCompletionsResponse.class),
    @JsonSubTypes.Type(value = DAPConfigurationDoneResponse.class),
    @JsonSubTypes.Type(value = DAPContinueResponse.class),
    @JsonSubTypes.Type(value = DAPDataBreakpointInfoResponse.class),
    @JsonSubTypes.Type(value = DAPDisassembleResponse.class),
    @JsonSubTypes.Type(value = DAPInitializeResponse.class),
    @JsonSubTypes.Type(value = DAPEvaluateResponse.class),
    @JsonSubTypes.Type(value = DAPExceptionInfoResponse.class),
    @JsonSubTypes.Type(value = DAPPauseResponse.class),
    @JsonSubTypes.Type(value = DAPLaunchResponse.class),
    @JsonSubTypes.Type(value = DAPNextResponse.class),
    @JsonSubTypes.Type(value = DAPScopesResponse.class),
    @JsonSubTypes.Type(value = DAPSetBreakpointsResponse.class),
    @JsonSubTypes.Type(value = DAPSetExpressionResponse.class),
    @JsonSubTypes.Type(value = DAPSetVariableResponse.class),
    @JsonSubTypes.Type(value = DAPStackTraceResponse.class),
    @JsonSubTypes.Type(value = DAPStepInResponse.class),
    @JsonSubTypes.Type(value = DAPStepInTargetsResponse.class),
    @JsonSubTypes.Type(value = DAPTerminateResponse.class),
    @JsonSubTypes.Type(value = DAPThreadsResponse.class),
    @JsonSubTypes.Type(value = DAPVariablesResponse.class),
})// extends DAPResponse
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DAPResponse extends DAPProtocolMessage {
    /**
     * Sequence number of the corresponding request.
     */
    @JsonProperty("request_seq")
    public int request_sequence;

    /**
     * Outcome of the request.
     * If true, the request was successful and the `body` attribute may contain
     * the result of the request.
     * If the value is false, the attribute `message` contains the error in short
     * form and the `body` may contain additional information (see
     * `ErrorResponse.body.error`).
     */
    @JsonProperty("success")
    public boolean success;

    /**
     * The command to requested.
     */
    public String command;

    /**
     * Contains the raw error in short form if `success` is false.
     * This raw error might be interpreted by the client and is not shown in the
     * UI.
     * Some predefined values exist.
     * Values:
     * 'cancelled': the request was cancelled.
     * 'notStopped': the request may be retried once the adapter is in a 'stopped'
     * state.
     * etc.
     */
    @JsonProperty("message")
    public String message;

    public String toString() {
        return String.format("Response '%s'%s", command, success ? "" : " failed '"+message+"'");
    }
}
