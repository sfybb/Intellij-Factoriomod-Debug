package factorio.debugger.DAP.messages.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.types.DAPSource;

@JsonTypeName("loadedSource")
public class DAPLoadedSourceEvent extends DAPEvent {
    @JsonProperty("body")
    public LoadedSourceBody body;
    public static class LoadedSourceBody {
        /**
         * The reason for the event.
         * Values: 'new', 'changed', 'removed'
         */
        @JsonProperty("reason")
        public LoadedSourceReason reason;
        public enum LoadedSourceReason {
            @JsonProperty("new")
            NEW,
            @JsonProperty("changed")
            CHANGED,
            @JsonProperty("removed")
            REMOVED
        }

        /**
         * The new, changed, or removed source.
         */
        @JsonProperty("source")
        public DAPSource source;
    }
    @Override
    public String toString() {
        return "Event: loaded source "+body.reason.name()+ " "+ body.source.name + (body.source.path != null ? " ("+body.source.path+")" : "");
    }
}
