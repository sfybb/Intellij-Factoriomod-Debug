package factorio.debugger.DAP.messages.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPEvent;
import factorio.debugger.DAP.messages.types.DAPModule;

public class DAPModuleEvent extends DAPEvent {
    @JsonProperty("body")
    public ModuleEventBody body;
    public static class ModuleEventBody {
        /**
         * The reason for the event.
         * Values: 'new', 'changed', 'removed'
         */
        @JsonProperty("reason")
        public Reason reason;
        public enum Reason {
            @JsonProperty("new")
            NEW,
            @JsonProperty("changed")
            CHANGED,
            @JsonProperty("removed")
            REMOVED
        }

        /**
         * The new, changed, or removed module. In case of `removed` only the module
         * id is used.
         */
        @JsonProperty("module")
        public DAPModule module;
    }

    @Override
    public String toString() {
        return String.format("%s: %s '%s'%s%s",
            super.toString(),
            body.reason.name(),
            body.module.id,
            body.module.version != null ? " v"+body.module.version : "",
            body.module.path != null ?
                " ("+body.module.path+")" :
                (body.module.symbolFilePath != null ?
                    "(S:'"+body.module.symbolFilePath+"')":
                    "")
        );
    }
}
