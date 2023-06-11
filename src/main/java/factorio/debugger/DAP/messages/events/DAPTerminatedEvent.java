package factorio.debugger.DAP.messages.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPEvent;

@JsonTypeName("terminated")
public class DAPTerminatedEvent extends DAPEvent {
    @JsonProperty("body")
    public TerminatedEventBody body;

    public static class TerminatedEventBody {
        /**
         * A debug adapter may set `restart` to true (or to an arbitrary object) to
         * request that the client restarts the session.
         * The value is not interpreted by the client and passed unmodified as an
         * attribute `__restart` to the `launch` and `attach` requests.
         */
        @JsonProperty("restart")
        @JsonRawValue
        public String restart;

        @Override
        public String toString() {
            return restart != null ? String.format("terminated with '%s'", restart) : "terminated";
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), body != null ? body.toString() : "");
    }
}
