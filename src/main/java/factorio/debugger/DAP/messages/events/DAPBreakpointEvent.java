package factorio.debugger.DAP.messages.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.types.DAPBreakpoint;

public class DAPBreakpointEvent extends DAPEvent {
    /**
     * Event-specific information.
     */
    @JsonProperty("body")
    public BreakpointEventBody body;

    public static class BreakpointEventBody {
        /**
         * The reason for the event.
         * Values: 'changed', 'new', 'removed', etc.
         */
        //'changed' | 'new' | 'removed' | string
        @JsonProperty("reason")
        public String reason;

        /**
         * The `id` attribute is used to find the target breakpoint, the other
         * attributes are used as the new values.
         */
        @JsonProperty("breakpoint")
        public DAPBreakpoint breakpoint;
    }
}
