package factorio.debugger.DAP.messages

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
open class DAPAdditionalProperties {
    @JvmField
    @JsonIgnore
    var additionalProperties: MutableMap<String, Any> = HashMap()

    // Capture all other fields that Jackson do not match other members
    @JsonAnyGetter
    fun otherFields(): Map<String, Any> {
        return additionalProperties
    }

    @JsonAnySetter
    fun setOtherField(name: String, value: Any) {
        additionalProperties[name] = value
    }
}
