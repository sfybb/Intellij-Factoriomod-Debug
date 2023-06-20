package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPCompletionItem

@JsonTypeName("completions")
class DAPCompletionsResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: CompletionsResponseBody

    class CompletionsResponseBody : DAPAdditionalProperties() {
        /**
         * The possible completions for .
         */
        @JsonProperty("targets")
        lateinit var targets: Array<DAPCompletionItem>
    }
}
