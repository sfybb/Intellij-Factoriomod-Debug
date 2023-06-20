package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPDisassembleRequest.DisassembleArguments

@JsonTypeName("disassemble")
class DAPDisassembleRequest(memoryReference: String, instructionCount: Int)
    : DAPRequest<DisassembleArguments>(DisassembleArguments(memoryReference, instructionCount)) {

    class DisassembleArguments(
        /**
         * Memory reference to the base location containing the instructions to
         * disassemble.
         */
        @field:JsonProperty("memoryReference") var memoryReference: String,
        /**
         * Number of instructions to disassemble starting at the specified location
         * and offset.
         * An adapter must return exactly this number of instructions - any
         * unavailable instructions should be replaced with an implementation-defined
         * 'invalid instruction' value.
         */
        @field:JsonProperty(
            "instructionCount"
        ) var instructionCount: Int
    ) : DAPAdditionalProperties() {
        /**
         * Offset (in bytes) to be applied to the reference location before
         * disassembling. Can be negative.
         */
        @JsonProperty("offset")
        var offset: Int? = null

        /**
         * Offset (in instructions) to be applied after the byte offset (if any)
         * before disassembling. Can be negative.
         */
        @JsonProperty("instructionOffset")
        var instructionOffset: Int? = null

        /**
         * If true, the adapter should attempt to resolve memory addresses and other
         * values to symbolic names.
         */
        @JsonProperty("resolveSymbols")
        var resolveSymbols: Boolean? = null
    }
}
