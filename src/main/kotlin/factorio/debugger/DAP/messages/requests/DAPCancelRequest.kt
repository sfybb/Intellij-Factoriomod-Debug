package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPCancelRequest.CancelRequestArguments

@JsonTypeName("cancel")
class DAPCancelRequest : DAPRequest<CancelRequestArguments> {
    constructor(requestId: Int?) : super(CancelRequestArguments(requestId))

    constructor(progressId: String?) : super(CancelRequestArguments(progressId))

    constructor(requestId: Int?, progressId: String?) : super(CancelRequestArguments(requestId, progressId))

    class CancelRequestArguments @JvmOverloads constructor(
        /**
         * The ID (attribute `seq`) of the request to cancel. If missing no request is
         * cancelled.
         * Both a `requestId` and a `progressId` can be specified in one request.
         */
        @field:JsonProperty("requestId") var requestId: Int?,
        /**
         * The ID (attribute `progressId`) of the progress to cancel. If missing no
         * progress is cancelled.
         * Both a `requestId` and a `progressId` can be specified in one request.
         */
        @field:JsonProperty(
            "progressId"
        ) var progressId: String? = null
    ) : DAPAdditionalProperties() {
        constructor(progressId: String?) : this(null, progressId)
    }
}
