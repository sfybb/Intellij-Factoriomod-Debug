package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPInitializeRequest.InitializeRequestArguments

@JsonTypeName("initialize")
class DAPInitializeRequest(adapterID: String) : DAPRequest<InitializeRequestArguments>(InitializeRequestArguments(adapterID)) {

    class InitializeRequestArguments(
        /**
         * The ID of the debug adapter.
         */
        @JsonProperty("adapterID")
        var adapterID: String
    ) : DAPAdditionalProperties() {

        /**
         * The ID of the client using this adapter.
         */
        @JvmField
        @JsonProperty("clientID")
        var clientID: String? = null

        /**
         * The human-readable name of the client using this adapter.
         */
        @JvmField
        @JsonProperty("clientName")
        var clientName: String? = null

        /**
         * The ISO-639 locale of the client using this adapter, e.g. en-US or de-CH.
         */
        @JvmField
        @JsonProperty("locale")
        var locale: String? = null

        /**
         * If true all line numbers are 1-based (default).
         */
        @JvmField
        @JsonProperty("linesStartAt1")
        var linesStartAt1: Boolean? = true

        /**
         * If true all column numbers are 1-based (default).
         */
        @JvmField
        @JsonProperty("columnsStartAt1")
        var columnsStartAt1: Boolean? = true

        /**
         * Determines in what format paths are specified. The default is `path`, which
         * is the native format.
         * Values: 'path', 'uri', etc.
         */
        @JvmField
        @JsonProperty("pathFormat")
        var pathFormat: String? = "path"

        /**
         * Client supports the `type` attribute for variables.
         */
        @JvmField
        @JsonProperty("supportsVariableType")
        var supportsVariableType: Boolean? = false

        /**
         * Client supports the paging of variables.
         */
        @JvmField
        @JsonProperty("supportsVariablePaging")
        var supportsVariablePaging: Boolean? = false

        /**
         * Client supports the `runInTerminal` request.
         */
        @JvmField
        @JsonProperty("supportsRunInTerminalRequest")
        var supportsRunInTerminalRequest: Boolean? = false

        /**
         * Client supports memory references.
         */
        @JvmField
        @JsonProperty("supportsMemoryReferences")
        var supportsMemoryReferences: Boolean? = false

        /**
         * Client supports progress reporting.
         */
        @JvmField
        @JsonProperty("supportsProgressReporting")
        var supportsProgressReporting: Boolean? = false

        /**
         * Client supports the `invalidated` event.
         */
        @JvmField
        @JsonProperty("supportsInvalidatedEvent")
        var supportsInvalidatedEvent: Boolean? = false

        /**
         * Client supports the `memory` event.
         */
        @JvmField
        @JsonProperty("supportsMemoryEvent")
        var supportsMemoryEvent: Boolean? = false

        /**
         * Client supports the `argsCanBeInterpretedByShell` attribute on the
         * `runInTerminal` request.
         */
        @JsonProperty("supportsArgsCanBeInterpretedByShell")
        var supportsArgsCanBeInterpretedByShell: Boolean? = false

        /**
         * Client supports the `startDebugging` request.
         */
        @JsonProperty("supportsStartDebuggingRequest")
        var supportsStartDebuggingRequest : Boolean?= false
    }
}
