package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPCompletionItem;

@JsonTypeName("completions")
public class DAPCompletionsResponse extends DAPResponse {
    @JsonProperty("body")
    public Body body;
    public static class Body {
        /**
         * The possible completions for .
         */
        @JsonProperty("targets")
        public DAPCompletionItem[] targets;
    }
}
