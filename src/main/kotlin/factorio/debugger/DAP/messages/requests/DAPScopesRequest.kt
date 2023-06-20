package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPScopesRequest.ScopesArguments

@JsonTypeName("scopes")
class DAPScopesRequest(frameId: Int) : DAPRequest<ScopesArguments>(ScopesArguments(frameId)) {

    class ScopesArguments(
        /**
         * Retrieve the scopes for the stack frame identified by `frameId`. The
         * `frameId` must have been obtained in the current suspended state. See
         * 'Lifetime of Object References' in the Overview section for details.
         */
        @field:JsonProperty("frameId") var frameId: Int
    ) : DAPAdditionalProperties()
}
