package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPStackFrame

@JsonTypeName("stackTrace")
class DAPStackTraceResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: StackTraceBody

    class StackTraceBody : DAPAdditionalProperties() {
        /**
         * The frames of the stack frame. If the array has length zero, there are no
         * stack frames available.
         * This means that there is no location information available.
         */
        @JsonProperty("stackFrames")
        lateinit var stackFrames: Array<DAPStackFrame>

        /**
         * The total number of frames available in the stack. If omitted or if
         * `totalFrames` is larger than the available frames, a client is expected
         * to request frames until a request returns less frames than requested
         * (which indicates the end of the stack). Returning monotonically
         * increasing `totalFrames` values for subsequent requests can be used to
         * enforce paging in the client.
         */
        @JsonProperty("totalFrames")
        var totalFrames: Int? = null
    }

    override fun toString(): String {
        return "Response: StackTrace (" + body.totalFrames + ")"
    }
}
