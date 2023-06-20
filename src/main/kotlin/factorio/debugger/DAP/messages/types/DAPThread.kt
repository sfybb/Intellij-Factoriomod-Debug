package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPThread : DAPAdditionalProperties() {
    /**
     * Unique identifier for the thread.
     */
    @JvmField
    @JsonProperty("id")
    var id: Int = 0

    /**
     * The name of the thread.
     */
    @JsonProperty("name")
    lateinit var name: String
}
