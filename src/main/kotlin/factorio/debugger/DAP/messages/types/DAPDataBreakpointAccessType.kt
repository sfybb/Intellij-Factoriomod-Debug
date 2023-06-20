package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class DAPDataBreakpointAccessType {
    @JsonProperty("read")
    READ,
    @JsonProperty("write")
    WRITE,
    @JsonProperty("readWrite")
    READ_WRITE;

    override fun toString(): String {
        return when(this) {
            READ -> "read"
            WRITE -> "write"
            READ_WRITE -> "readWrite"
        }
    }
}
