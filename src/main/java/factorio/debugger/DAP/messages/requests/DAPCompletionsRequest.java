package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("completions")
public class DAPCompletionsRequest extends DAPRequest<DAPCompletionsRequest.Arguments> {
    public DAPCompletionsRequest(int stackFrameId, String text, int line, int column) {
        this.arguments = new Arguments(stackFrameId, text, line, column);
    }

    public DAPCompletionsRequest(int stackFrameId, String text, int column) {
        this.arguments = new Arguments(stackFrameId, text, column);
    }
    public static class Arguments extends DAPAdditionalProperties {
        public Arguments(int stackFrameId, String text, int column) {
            this.text = text;
            this.column = column;
            this.frameId = stackFrameId;
            this.line = null;
        }
        public Arguments(int stackFrameId, String text, int line, int column) {
            this.frameId = stackFrameId;
            this.text = text;
            this.column = column;
            this.line = line;
        }
        /**
         * Returns completions in the scope of this stack frame. If not specified, the
         * completions are returned for the global scope.
         */
        @JsonProperty("frameId")
        public Integer frameId;

        /**
         * One or more source lines. Typically this is the text users have typed into
         * the debug console before they asked for completion.
         */
        @JsonProperty("text")
        public String text;

        /**
         * The position within `text` for which to determine the completion proposals.
         * It is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based.
         */
        @JsonProperty("column")
        public int column;

        /**
         * A line for which to determine the completion proposals. If missing the
         * first line of the text is assumed.
         */
        @JsonProperty("line")
        public Integer line;
    }
}
