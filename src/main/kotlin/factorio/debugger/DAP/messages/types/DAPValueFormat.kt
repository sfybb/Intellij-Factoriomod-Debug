package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPValueFormat : DAPAdditionalProperties() {
    /**
     * Display the value in hex.
     */
    @JsonProperty("hex")
    var hex: Boolean? = null
}
