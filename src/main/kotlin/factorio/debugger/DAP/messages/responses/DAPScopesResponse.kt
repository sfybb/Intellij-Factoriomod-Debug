package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPScope

@JsonTypeName("scopes")
class DAPScopesResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: ScopesResponseBody

    class ScopesResponseBody : DAPAdditionalProperties() {
        /**
         * The scopes of the stack frame. If the array has length zero, there are no
         * scopes available.
         */
        @JsonProperty("scopes")
        lateinit var scopes: Array<DAPScope>
    }

    override fun toString(): String {
        return "${super.toString()}: #scopes: ${body.scopes.size}"
    }
}
