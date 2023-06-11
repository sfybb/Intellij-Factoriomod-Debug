package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPDisassembledInstruction;

@JsonTypeName("disassemble")
public class DAPDisassembleResponse extends DAPResponse {
    @JsonProperty("body")
    public DisassembleResponseBody body;
    public static class DisassembleResponseBody {
        /**
         * The list of disassembled instructions.
         */
        @JsonProperty("instructions")
        public DAPDisassembledInstruction[] instructions;
    }
}
