package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DAPSteppingGranularity {
    @JsonProperty("statement")
    STATEMENT,
    @JsonProperty("line")
    LINE,
    @JsonProperty("instruction")
    INSTRUCTION
}
