package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPDisassembledInstruction

@JsonTypeName("disassemble")
class DAPDisassembleResponse : DAPResponse() {
    @JsonProperty("body")
    var body: DisassembleResponseBody? = null

    class DisassembleResponseBody : DAPAdditionalProperties() {
        /**
         * The list of disassembled instructions.
         */
        @JsonProperty("instructions")
        lateinit var instructions: Array<DAPDisassembledInstruction>
    }
}
