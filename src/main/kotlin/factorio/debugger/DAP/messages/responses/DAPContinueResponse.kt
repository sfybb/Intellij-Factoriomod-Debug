package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties

@JsonTypeName("continue")
class DAPContinueResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: ContinueResponseBody

    class ContinueResponseBody : DAPAdditionalProperties() {
        /**
         * The value true (or a missing property) signals to the client that all
         * threads have been resumed. The value false indicates that not all threads
         * were resumed.
         */
        @JsonProperty("allThreadsContinued")
        var allThreadsContinued: Boolean? = null
    }

    override fun toString(): String {
        return "${super.toString()} ${if (body.allThreadsContinued == false) "only requested thread continued" else "all threads continued"}"
    }
}
