package factorio.debugger.DAP.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import factorio.debugger.DAP.messages.events.DAPBreakpointEvent;
import factorio.debugger.DAP.messages.events.DAPInitializedEvent;
import factorio.debugger.DAP.messages.events.DAPLoadedSourceEvent;
import factorio.debugger.DAP.messages.events.DAPModuleEvent;
import factorio.debugger.DAP.messages.events.DAPOutputEvent;
import factorio.debugger.DAP.messages.events.DAPStoppedEvent;
import factorio.debugger.DAP.messages.events.DAPTerminatedEvent;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "event",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DAPBreakpointEvent.class, name = "breakpoint"),
    @JsonSubTypes.Type(value = DAPStoppedEvent.class, name = "stopped"),
    @JsonSubTypes.Type(value = DAPOutputEvent.class, name = "output"),
    @JsonSubTypes.Type(value = DAPLoadedSourceEvent.class, name = "loadedSource"),
    @JsonSubTypes.Type(value = DAPModuleEvent.class, name = "module"),
    @JsonSubTypes.Type(value = DAPInitializedEvent.class, name = "initialized"),
    @JsonSubTypes.Type(value = DAPTerminatedEvent.class, name = "terminated"),
})// extends DAPRequest
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DAPEvent extends DAPProtocolMessage {
    /**
     * Type of event.
     */
    public String event;

    public DAPEventNames getEventId() {
        if (event == null) return null;
        return DAPEventNames.valueOf(event.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("Event: '%s'", event);
    }
}
