package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPStackTraceRequest.StackTraceArguments
import factorio.debugger.DAP.messages.types.DAPStackFrameFormat

@JsonTypeName("stackTrace")
class DAPStackTraceRequest(threadId: Int) : DAPRequest<StackTraceArguments>(StackTraceArguments(threadId)) {

    class StackTraceArguments(
        /**
         * Retrieve the stacktrace for this thread.
         */
        @JsonProperty("threadId")
        var threadId: Int = 0
    ) : DAPAdditionalProperties() {
        /**
         * The index of the first frame to return; if omitted frames start at 0.
         */
        @JsonProperty("startFrame")
        var startFrame: Int? = null

        /**
         * The maximum number of frames to return. If levels is not specified or 0,
         * all frames are returned.
         */
        @JvmField
        @JsonProperty("levels")
        var levels: Int? = null

        /**
         * Specifies details on how to format the stack frames.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        var format: DAPStackFrameFormat? = null
    }
}
