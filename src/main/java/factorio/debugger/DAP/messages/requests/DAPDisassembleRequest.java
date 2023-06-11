package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("disassemble")
public class DAPDisassembleRequest extends DAPRequest<DAPDisassembleRequest.DisassembleArguments> {
    public DAPDisassembleRequest(final String memoryReference, final int instructionCount) {
        this.arguments = new DisassembleArguments(memoryReference, instructionCount);
    }

    public static class DisassembleArguments extends DAPAdditionalProperties {
        /**
         * Memory reference to the base location containing the instructions to
         * disassemble.
         */
        @JsonProperty("memoryReference")
        public String memoryReference;

        /**
         * Offset (in bytes) to be applied to the reference location before
         * disassembling. Can be negative.
         */
        @JsonProperty("offset")
        public Integer offset;

        /**
         * Offset (in instructions) to be applied after the byte offset (if any)
         * before disassembling. Can be negative.
         */
        @JsonProperty("instructionOffset")
        public Integer instructionOffset;

        /**
         * Number of instructions to disassemble starting at the specified location
         * and offset.
         * An adapter must return exactly this number of instructions - any
         * unavailable instructions should be replaced with an implementation-defined
         * 'invalid instruction' value.
         */
        @JsonProperty("instructionCount")
        public int instructionCount;

        /**
         * If true, the adapter should attempt to resolve memory addresses and other
         * values to symbolic names.
         */
        @JsonProperty("resolveSymbols")
        public Boolean resolveSymbols;

        public DisassembleArguments(final String memoryReference, final int instructionCount) {
            this.memoryReference = memoryReference;
            this.instructionCount = instructionCount;
        }
    }
}
