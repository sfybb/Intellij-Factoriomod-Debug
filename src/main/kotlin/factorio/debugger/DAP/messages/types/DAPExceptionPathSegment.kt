package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DAPExceptionPathSegment {
    /**
     * If false or missing this segment matches the names provided, otherwise it
     * matches anything except the names provided.
     */
    @JsonProperty("negate")
    public Boolean negate;

    /**
     * Depending on the value of `negate` the names that should match or not
     * match.
     */
    @JsonProperty("names")
    public String[] names;
}
