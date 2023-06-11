package factorio.debugger.DAP.messages;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DAPAdditionalProperties {
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<>();

    // Capture all other fields that Jackson do not match other members
    @JsonAnyGetter
    public Map<String, Object> otherFields() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setOtherField(String name, Object value) {
        additionalProperties.put(name, value);
    }
}
