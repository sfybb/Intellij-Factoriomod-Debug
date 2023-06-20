package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import factorio.debugger.DAP.messages.DAPEventNames
import factorio.debugger.DAP.messages.DAPProtocolMessage
import factorio.debugger.DAP.messages.events.*
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "event", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = DAPBreakpointEvent::class, name = "breakpoint"),
    JsonSubTypes.Type(value = DAPStoppedEvent::class, name = "stopped"),
    JsonSubTypes.Type(value = DAPOutputEvent::class, name = "output"),
    JsonSubTypes.Type(value = DAPLoadedSourceEvent::class, name = "loadedSource"),
    JsonSubTypes.Type(value = DAPModuleEvent::class, name = "module"),
    JsonSubTypes.Type(value = DAPInitializedEvent::class, name = "initialized"),
    JsonSubTypes.Type(value = DAPTerminatedEvent::class, name = "terminated")
) // extends DAPRequest
@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class DAPEvent : DAPProtocolMessage() {
    /**
     * Type of event.
     */
    var event: String? = null
    val eventId: DAPEventNames?
        get() = event?.let { DAPEventNames.valueOf(it.uppercase(Locale.getDefault())) }

    override fun toString(): String {
        return String.format("Event: '%s'", event)
    }
}
