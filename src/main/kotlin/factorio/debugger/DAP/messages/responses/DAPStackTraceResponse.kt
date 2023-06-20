package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPStackFrame;

@JsonTypeName("stackTrace")
public class DAPStackTraceResponse extends DAPResponse {
    @JsonProperty("body")
    public StackTraceBody body;
    public static class StackTraceBody extends DAPAdditionalProperties {
        /**
         * The frames of the stack frame. If the array has length zero, there are no
         * stack frames available.
         * This means that there is no location information available.
         */
        @JsonProperty("stackFrames")
        public DAPStackFrame[] stackFrames;

        /**
         * The total number of frames available in the stack. If omitted or if
         * `totalFrames` is larger than the available frames, a client is expected
         * to request frames until a request returns less frames than requested
         * (which indicates the end of the stack). Returning monotonically
         * increasing `totalFrames` values for subsequent requests can be used to
         * enforce paging in the client.
         */
        @JsonProperty("totalFrames")
        public Integer totalFrames;
    }

    @Override
    public String toString() {
        return "Response: StackTrace ("+ body.totalFrames +")";
    }
}
