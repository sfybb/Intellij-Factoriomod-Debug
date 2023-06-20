package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPModule

@JsonTypeName("module")
class DAPModuleEvent : DAPEvent() {
    @JsonProperty("body")
    lateinit var body: ModuleEventBody

    class ModuleEventBody : DAPAdditionalProperties() {
        /**
         * The reason for the event.
         * Values: 'new', 'changed', 'removed'
         */
        @JsonProperty("reason")
        lateinit var reason: Reason

        /**
         * The new, changed, or removed module. In case of `removed` only the module
         * id is used.
         */
        @JsonProperty("module")
        lateinit var module: DAPModule
    }

    enum class Reason {
        @JsonProperty("new")
        NEW,
        @JsonProperty("changed")
        CHANGED,
        @JsonProperty("removed")
        REMOVED
    }

    override fun toString(): String {
        return "Event: module '${body.reason.name}': ${body.module}"
    }
}
