package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPBreakpoint

class DAPBreakpointEvent : DAPEvent() {
    /**
     * Event-specific information.
     */
    @JvmField
    @JsonProperty("body")
    var body: BreakpointEventBody? = null

    class BreakpointEventBody : DAPAdditionalProperties() {
        /**
         * The reason for the event.
         * Values: 'changed', 'new', 'removed', etc.
         */
        //'changed' | 'new' | 'removed' | string
        @JvmField
        @JsonProperty("reason")
        var reason: String? = null

        /**
         * The `id` attribute is used to find the target breakpoint, the other
         * attributes are used as the new values.
         */
        @JvmField
        @JsonProperty("breakpoint")
        var breakpoint: DAPBreakpoint? = null
    }
}
