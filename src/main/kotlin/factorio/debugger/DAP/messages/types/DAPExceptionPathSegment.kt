package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPExceptionPathSegment : DAPAdditionalProperties() {
    /**
     * If false or missing this segment matches the names provided, otherwise it
     * matches anything except the names provided.
     */
    @JsonProperty("negate")
    var negate: Boolean? = null

    /**
     * Depending on the value of `negate` the names that should match or not
     * match.
     */
    @JsonProperty("names")
    lateinit var names: Array<String>
}
