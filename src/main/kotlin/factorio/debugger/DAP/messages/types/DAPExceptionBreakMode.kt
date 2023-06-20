package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class DAPExceptionBreakMode {
    @JsonProperty("never")
    NEVER,
    @JsonProperty("always")
    ALWAYS,
    @JsonProperty("unhandled")
    UNHANDLED,
    @JsonProperty("userUnhandled")
    USER_UNHANDLED
    ;

    override fun toString(): String {
        return when(this) {
            NEVER -> "never"
            ALWAYS -> "always"
            UNHANDLED -> "unhandled"
            USER_UNHANDLED -> "userUnhandled"
        }
    }
}
