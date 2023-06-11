package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;

@JsonTypeName("continue")
public class DAPContinueResponse extends DAPResponse {
    @JsonProperty("body")
    public ContinueResponseBody body;
    public static class ContinueResponseBody {
        /**
         * The value true (or a missing property) signals to the client that all
         * threads have been resumed. The value false indicates that not all threads
         * were resumed.
         */
        @JsonProperty("allThreadsContinued")
        public Boolean allThreadsContinued;
    }
}
