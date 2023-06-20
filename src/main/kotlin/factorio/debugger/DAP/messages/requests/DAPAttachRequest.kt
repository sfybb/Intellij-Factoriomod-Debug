package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPAttachRequest.AttachRequestArguments

@JsonTypeName("attach")
class DAPAttachRequest : DAPRequest<AttachRequestArguments>(AttachRequestArguments()) {
    class AttachRequestArguments : DAPAdditionalProperties() {
        /**
         * Arbitrary data from the previous, restarted session.
         * The data is sent as the `restart` attribute of the `terminated` event.
         * The client should leave the data intact.
         */
        @JsonProperty("__restart")
        @JsonRawValue
        var __restart: String? = null
    }
}
