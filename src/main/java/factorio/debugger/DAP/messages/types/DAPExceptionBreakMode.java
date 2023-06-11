package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DAPExceptionBreakMode {
    @JsonProperty("never") NEVER,
    @JsonProperty("always") ALWAYS,
    @JsonProperty("unhandled") UNHANDLED,
    @JsonProperty("userUnhandled") USER_UNHANDLED;
}
