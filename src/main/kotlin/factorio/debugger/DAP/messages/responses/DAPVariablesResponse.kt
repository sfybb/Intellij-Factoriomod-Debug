package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPVariable

@JsonTypeName("variables")
class DAPVariablesResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: VariablesResponseBody

    class VariablesResponseBody : DAPAdditionalProperties() {
        /**
         * All (or a range) of variables for the given variable reference.
         */
        @JsonProperty("variables")
        lateinit var variables: Array<DAPVariable>
    }
}
