package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPTerminateRequest.TerminateArguments

@JsonTypeName("terminate")
class DAPTerminateRequest : DAPRequest<TerminateArguments>(TerminateArguments()) {

    class TerminateArguments : DAPAdditionalProperties() {
        /**
         * A value of true indicates that this `terminate` request is part of a
         * restart sequence.
         */
        @JsonProperty("restart")
        var restart: Boolean? = false
    }
}
