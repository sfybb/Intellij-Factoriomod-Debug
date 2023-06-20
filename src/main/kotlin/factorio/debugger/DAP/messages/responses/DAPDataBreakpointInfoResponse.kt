package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPDataBreakpointAccessType;

@JsonTypeName("dataBreakpointInfo")
public class DAPDataBreakpointInfoResponse extends DAPResponse {
    @JsonProperty("body")
    public DataBreakpointInfoResponseBody body;

    public static class DataBreakpointInfoResponseBody {
        /**
         * An identifier for the data on which a data breakpoint can be registered
         * with the `setDataBreakpoints` request or null if no data breakpoint is
         * available.
         */
        @JsonProperty("dataId")
        public String dataId;

        /**
         * UI string that describes on what data the breakpoint is set on or why a
         * data breakpoint is not available.
         */
        @JsonProperty("description")
        public String description;

        /**
         * Attribute lists the available access types for a potential data
         * breakpoint. A UI client could surface this information.
         */
        @JsonProperty("accessTypes")
        public DAPDataBreakpointAccessType[] accessTypes;

        /**
         * Attribute indicates that a potential data breakpoint could be persisted
         * across sessions.
         */
        @JsonProperty("canPersist")
        public Boolean canPersist;
    }
}
