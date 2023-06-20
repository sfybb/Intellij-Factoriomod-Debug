package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import factorio.debugger.DAP.messages.DAPProtocolMessage
import factorio.debugger.DAP.messages.responses.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "command", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = DAPBreakpointLocationsResponse::class),
    JsonSubTypes.Type(value = DAPCancelResponse::class),
    JsonSubTypes.Type(value = DAPCompletionsResponse::class),
    JsonSubTypes.Type(value = DAPConfigurationDoneResponse::class),
    JsonSubTypes.Type(value = DAPContinueResponse::class),
    JsonSubTypes.Type(value = DAPDataBreakpointInfoResponse::class),
    JsonSubTypes.Type(value = DAPDisassembleResponse::class),
    JsonSubTypes.Type(value = DAPInitializeResponse::class),
    JsonSubTypes.Type(value = DAPEvaluateResponse::class),
    JsonSubTypes.Type(value = DAPExceptionInfoResponse::class),
    JsonSubTypes.Type(value = DAPPauseResponse::class),
    JsonSubTypes.Type(value = DAPLaunchResponse::class),
    JsonSubTypes.Type(value = DAPNextResponse::class),
    JsonSubTypes.Type(value = DAPScopesResponse::class),
    JsonSubTypes.Type(value = DAPSetBreakpointsResponse::class),
    JsonSubTypes.Type(value = DAPSetExpressionResponse::class),
    JsonSubTypes.Type(value = DAPSetVariableResponse::class),
    JsonSubTypes.Type(value = DAPStackTraceResponse::class),
    JsonSubTypes.Type(value = DAPStepInResponse::class),
    JsonSubTypes.Type(value = DAPStepInTargetsResponse::class),
    JsonSubTypes.Type(value = DAPTerminateResponse::class),
    JsonSubTypes.Type(value = DAPThreadsResponse::class),
    JsonSubTypes.Type(value = DAPVariablesResponse::class)
) // extends DAPResponse
@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class DAPResponse : DAPProtocolMessage() {
    /**
     * Sequence number of the corresponding request.
     */
    @JvmField
    @JsonProperty("request_seq")
    var requestSequence: Int = 0

    /**
     * Outcome of the request.
     * If true, the request was successful and the `body` attribute may contain
     * the result of the request.
     * If the value is false, the attribute `message` contains the error in short
     * form and the `body` may contain additional information (see
     * `ErrorResponse.body.error`).
     */
    @JvmField
    @JsonProperty("success")
    var success: Boolean = false

    /**
     * The command to requested.
     */
    var command: String? = null

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
    @JvmField
    @JsonProperty("message")
    var message: String? = null
    override fun toString(): String {
        return "Response '$command'${if (success) "" else " failed '$message'"}"
    }
}
