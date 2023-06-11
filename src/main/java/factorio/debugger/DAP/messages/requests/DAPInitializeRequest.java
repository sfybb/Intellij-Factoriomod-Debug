package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("initialize")
public class DAPInitializeRequest extends DAPRequest<DAPInitializeRequest.InitializeRequestArguments> {
    public DAPInitializeRequest() {
        arguments = new InitializeRequestArguments();
    }

    public static class InitializeRequestArguments extends DAPAdditionalProperties {
        /**
         * The ID of the client using this adapter.
         */
        @JsonProperty("clientID")
        public String clientID;

        /**
         * The human-readable name of the client using this adapter.
         */
        @JsonProperty("clientName")
        public String clientName;

        /**
         * The ID of the debug adapter.
         */
        @JsonProperty("adapterID")
        public String adapterID;

        /**
         * The ISO-639 locale of the client using this adapter, e.g. en-US or de-CH.
         */
        @JsonProperty("locale")
        public String locale;

        /**
         * If true all line numbers are 1-based (default).
         */
        @JsonProperty("linesStartAt1")
        public boolean linesStartAt1 = true;

        /**
         * If true all column numbers are 1-based (default).
         */
        @JsonProperty("columnsStartAt1")
        public boolean columnsStartAt1 = true;

        /**
         * Determines in what format paths are specified. The default is `path`, which
         * is the native format.
         * Values: 'path', 'uri', etc.
         */
        @JsonProperty("pathFormat")
        public String pathFormat = "path";

        /**
         * Client supports the `type` attribute for variables.
         */
        @JsonProperty("supportsVariableType")
        public boolean supportsVariableType;

        /**
         * Client supports the paging of variables.
         */
        @JsonProperty("supportsVariablePaging")
        public boolean supportsVariablePaging;

        /**
         * Client supports the `runInTerminal` request.
         */
        @JsonProperty("supportsRunInTerminalRequest")
        public boolean supportsRunInTerminalRequest;

        /**
         * Client supports memory references.
         */
        @JsonProperty("supportsMemoryReferences")
        public boolean supportsMemoryReferences;

        /**
         * Client supports progress reporting.
         */
        @JsonProperty("supportsProgressReporting")
        public boolean supportsProgressReporting;

        /**
         * Client supports the `invalidated` event.
         */
        @JsonProperty("supportsInvalidatedEvent")
        public boolean supportsInvalidatedEvent;

        /**
         * Client supports the `memory` event.
         */
        @JsonProperty("supportsMemoryEvent")
        public boolean supportsMemoryEvent;

        /**
         * Client supports the `argsCanBeInterpretedByShell` attribute on the
         * `runInTerminal` request.
         */
        @JsonProperty("supportsArgsCanBeInterpretedByShell")
        public boolean supportsArgsCanBeInterpretedByShell;

        /**
         * Client supports the `startDebugging` request.
         */
        @JsonProperty("supportsStartDebuggingRequest")
        public boolean supportsStartDebuggingRequest;
    }
}
