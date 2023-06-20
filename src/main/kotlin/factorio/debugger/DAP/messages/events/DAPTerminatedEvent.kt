package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties

@JsonTypeName("terminated")
class DAPTerminatedEvent : DAPEvent() {
    @JsonProperty("body")
    var body: TerminatedEventBody? = null

    class TerminatedEventBody : DAPAdditionalProperties() {
        /**
         * A debug adapter may set `restart` to true (or to an arbitrary object) to
         * request that the client restarts the session.
         * The value is not interpreted by the client and passed unmodified as an
         * attribute `__restart` to the `launch` and `attach` requests.
         */
        @JsonProperty("restart")
        @JsonRawValue
        var restart: String? = null
    }

    override fun toString(): String {
        return "Event: terminated ${body?.restart?.let{ "and restart with '$it'" }}"
    }
}
