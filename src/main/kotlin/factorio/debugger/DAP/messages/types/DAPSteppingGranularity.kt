package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class DAPSteppingGranularity {
    @JsonProperty("statement")
    STATEMENT,
    @JsonProperty("line")
    LINE,
    @JsonProperty("instruction")
    INSTRUCTION;

    override fun toString(): String {
        return this.name.lowercase()
    }
}
