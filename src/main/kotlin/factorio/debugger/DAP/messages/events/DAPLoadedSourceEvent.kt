package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPSource

@JsonTypeName("loadedSource")
class DAPLoadedSourceEvent : DAPEvent() {
    @JsonProperty("body")
    lateinit var body: LoadedSourceBody

    class LoadedSourceBody : DAPAdditionalProperties() {
        /**
         * The reason for the event.
         * Values: 'new', 'changed', 'removed'
         */
        @JsonProperty("reason")
        lateinit var reason: LoadedSourceReason

        /**
         * The new, changed, or removed source.
         */
        @JsonProperty("source")
        lateinit var source: DAPSource
    }

    enum class LoadedSourceReason {
        @JsonProperty("new")
        NEW,
        @JsonProperty("changed")
        CHANGED,
        @JsonProperty("removed")
        REMOVED
    }

    override fun toString(): String {
        return "Event: loaded source '${body.reason.name}' ${body.source}"
    }
}
