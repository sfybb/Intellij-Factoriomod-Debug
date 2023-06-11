package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DAPDataBreakpointAccessType {
    @JsonProperty("read") READ,
    @JsonProperty("write") WRITE,
    @JsonProperty("readWrite") READ_WRITE;
}
