package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPStackFrameFormat;

@JsonTypeName("stackTrace")
public class DAPStackTraceRequest extends DAPRequest<DAPStackTraceRequest.StackTraceArguments> {
    public DAPStackTraceRequest(int threadId) {
        arguments = new StackTraceArguments(threadId);
    }
    public static class StackTraceArguments extends DAPAdditionalProperties {
        public StackTraceArguments(int threadId) {

        }

        /**
         * Retrieve the stacktrace for this thread.
         */
        @JsonProperty("threadId")
        public int threadId;

        /**
         * The index of the first frame to return; if omitted frames start at 0.
         */
        @JsonProperty("startFrame")
        public Integer startFrame;

        /**
         * The maximum number of frames to return. If levels is not specified or 0,
         * all frames are returned.
         */
        @JsonProperty("levels")
        public Integer levels;

        /**
         * Specifies details on how to format the stack frames.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        public DAPStackFrameFormat format;
    }
}
