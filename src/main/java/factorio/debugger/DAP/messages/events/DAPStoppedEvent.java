package factorio.debugger.DAP.messages.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPEvent;

@JsonTypeName("stopped")
public class DAPStoppedEvent extends DAPEvent {
    /**
     * Event-specific information.
     */
    @JsonProperty("body")
    public StoppedEventBody body;

    public static class StoppedEventBody {
        /**
         * The reason for the event.
         * For backward compatibility this string is shown in the UI if the
         * `description` attribute is missing (but it must not be translated).
         * Values: 'step', 'breakpoint', 'exception', 'pause', 'entry', 'goto',
         * 'function breakpoint', 'data breakpoint', 'instruction breakpoint', etc.
         */
        @JsonProperty("reason")
        public SoppedReason reason;
        public enum SoppedReason {
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

        /**
         * The full reason for the event, e.g. 'Paused on exception'. This string is
         * shown in the UI as is and can be translated.
         */
        @JsonProperty("description")
        public String description;

        /**
         * The thread which was stopped.
         */
        @JsonProperty("threadId")
        public Integer threadId;

        /**
         * A value of true hints to the client that this event should not change the
         * focus.
         */
        @JsonProperty("preserveFocusHint")
        public Boolean preserveFocusHint;

        /**
         * Additional information. E.g. if reason is `exception`, text contains the
         * exception name. This string is shown in the UI.
         */
        @JsonProperty("text")
        public String text;

        /**
         * If `allThreadsStopped` is true, a debug adapter can announce that all
         * threads have stopped.
         * - The client should use this information to enable that all threads can
         * be expanded to access their stacktraces.
         * - If the attribute is missing or false, only the thread with the given
         * `threadId` can be expanded.
         */
        @JsonProperty("allThreadsStopped")
        public Boolean allThreadsStopped;

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
        @JsonProperty("hitBreakpointIds")
        public Integer[] hitBreakpointIds;
    }

    @Override
    public String toString() {
        return "Event: stopped "+body.reason + (body.description != null ? " ("+body.description+")" : "")  +
            (body.allThreadsStopped == null || !body.allThreadsStopped ? " stopped thread "+body.threadId : " stopped all threads");
    }
}
