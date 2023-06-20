package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPValueFormat extends DAPAdditionalProperties {
    /**
     * Display the value in hex.
     */
    @JsonProperty("hex")
    public Boolean hex;
}
