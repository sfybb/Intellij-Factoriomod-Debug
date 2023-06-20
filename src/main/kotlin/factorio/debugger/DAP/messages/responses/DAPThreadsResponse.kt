package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPThread;

@JsonTypeName("threads")
public class DAPThreadsResponse extends DAPResponse {
    @JsonProperty("body")
    public ThreadsResponseBody body;

    public static class ThreadsResponseBody extends DAPAdditionalProperties {
        /**
         * All threads.
         */
        @JsonProperty("threads")
        public DAPThread[] threads;
    }
}
