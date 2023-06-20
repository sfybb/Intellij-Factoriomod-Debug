package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPThread

@JsonTypeName("threads")
class DAPThreadsResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: ThreadsResponseBody

    class ThreadsResponseBody : DAPAdditionalProperties() {
        /**
         * All threads.
         */
        @JsonProperty("threads")
        lateinit var threads: Array<DAPThread>
    }
}
