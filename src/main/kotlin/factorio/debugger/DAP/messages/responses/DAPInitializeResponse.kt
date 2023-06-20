package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.types.DAPCapabilities

@JsonTypeName("initialize")
class DAPInitializeResponse : DAPResponse() {
    @JvmField
    @JsonProperty("body")
    var body: DAPCapabilities? = null
    override fun toString(): String {
        return "${super.toString()} Capabilities: ${body ?: "[]"}"
    }
}
