package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPCompletionsRequest.CompletionsRequestArguments

@JsonTypeName("completions")
class DAPCompletionsRequest : DAPRequest<CompletionsRequestArguments> {
    constructor(stackFrameId: Int, text: String, line: Int, column: Int) :
            super(CompletionsRequestArguments(stackFrameId, text, line, column))

    constructor(stackFrameId: Int, text: String, column: Int)
            : super(CompletionsRequestArguments(stackFrameId, text, column))

    class CompletionsRequestArguments : DAPAdditionalProperties {
        constructor(stackFrameId: Int, text: String, column: Int) {
            this.text = text
            this.column = column
            this.frameId = stackFrameId
            this.line = null
        }

        constructor(stackFrameId: Int, text: String, line: Int, column: Int) {
            this.frameId = stackFrameId
            this.text = text
            this.column = column
            this.line = line
        }

        /**
         * Returns completions in the scope of this stack frame. If not specified, the
         * completions are returned for the global scope.
         */
        @JsonProperty("frameId")
        var frameId: Int?

        /**
         * One or more source lines. Typically this is the text users have typed into
         * the debug console before they asked for completion.
         */
        @JsonProperty("text")
        var text: String

        /**
         * The position within `text` for which to determine the completion proposals.
         * It is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based.
         */
        @JsonProperty("column")
        var column: Int

        /**
         * A line for which to determine the completion proposals. If missing the
         * first line of the text is assumed.
         */
        @JsonProperty("line")
        var line: Int?
    }
}
