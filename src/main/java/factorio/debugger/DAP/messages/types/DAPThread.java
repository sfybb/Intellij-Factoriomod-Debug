package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPThread extends DAPAdditionalProperties {
    /**
     * Unique identifier for the thread.
     */
    @JsonProperty("id")
    public Integer id;

    /**
     * The name of the thread.
     */
    @JsonProperty("name")
    public String name;
}
