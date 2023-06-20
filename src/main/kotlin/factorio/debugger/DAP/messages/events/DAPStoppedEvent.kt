package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties

@JsonTypeName("stopped")
class DAPStoppedEvent : DAPEvent() {
    /**
     * Event-specific information.
     */
    @JsonProperty("body")
    lateinit var body: StoppedEventBody

    class StoppedEventBody : DAPAdditionalProperties() {
        /**
         * The reason for the event.
         * For backward compatibility this string is shown in the UI if the
         * `description` attribute is missing (but it must not be translated).
         * Values: 'step', 'breakpoint', 'exception', 'pause', 'entry', 'goto',
         * 'function breakpoint', 'data breakpoint', 'instruction breakpoint', etc.
         */
        @JsonProperty("reason")
        lateinit var reason: SoppedReason

        /**
         * The full reason for the event, e.g. 'Paused on exception'. This string is
         * shown in the UI as is and can be translated.
         */
        @JsonProperty("description")
        var description: String? = null

        /**
         * The thread which was stopped.
         */
        @JvmField
        @JsonProperty("threadId")
        var threadId: Int? = null

        /**
         * A value of true hints to the client that this event should not change the
         * focus.
         */
        @JsonProperty("preserveFocusHint")
        var preserveFocusHint: Boolean? = null

        /**
         * Additional information. E.g. if reason is `exception`, text contains the
         * exception name. This string is shown in the UI.
         */
        @JsonProperty("text")
        var text: String? = null

        /**
         * If `allThreadsStopped` is true, a debug adapter can announce that all
         * threads have stopped.
         * - The client should use this information to enable that all threads can
         * be expanded to access their stacktraces.
         * - If the attribute is missing or false, only the thread with the given
         * `threadId` can be expanded.
         */
        @JvmField
        @JsonProperty("allThreadsStopped")
        var allThreadsStopped: Boolean? = null

        /**
         * Ids of the breakpoints that triggered the event. In most cases there is
         * only a single breakpoint but here are some examples for multiple
         * breakpoints:
         * - Different types of breakpoints map to the same location.
         * - Multiple source breakpoints get collapsed to the same instruction by
         * the compiler/runtime.
         * - Multiple function breakpoints with different function names map to the
         * same location.
         */
        @JvmField
        @JsonProperty("hitBreakpointIds")
        var hitBreakpointIds: Array<Int> = arrayOf()
    }

    enum class SoppedReason {
        @JsonProperty("step")
        SETP,
        @JsonProperty("breakpoint")
        BREAKPOINT,
        @JsonProperty("exception")
        EXCEPTON,
        @JsonProperty("pause")
        PAUSE,
        @JsonProperty("entry")
        ENTRY,
        @JsonProperty("goto")
        GOTO,
        @JsonProperty("function breakpoint")
        FUNCTION_BREAKPOINT,
        @JsonProperty("data breakpoint")
        DATA_BREAKPOINT,
        @JsonProperty("instruction breakpoint")
        INSTRUCTION_BREAKPOINT
    }

    override fun toString(): String {
        return "Event: stopped ${body.reason} ${body.description?.let{"($it "} ?: ""}${
            if (body.allThreadsStopped == true) "stopped all threads" 
            else "stopped thread ${body.threadId}" }"
    }
}
