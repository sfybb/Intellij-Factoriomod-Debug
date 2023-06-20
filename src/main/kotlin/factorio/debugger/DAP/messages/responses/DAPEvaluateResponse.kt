package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPVariablePresentationHint;

@JsonTypeName("evaluate")
public class DAPEvaluateResponse extends DAPResponse {
    @JsonProperty("body")
    public Body body;
    public static class Body extends DAPAdditionalProperties {
        /**
         * The result of the evaluate request.
         */
        @JsonProperty("result")
        public String result;

        /**
         * The type of the evaluate result.
         * This attribute should only be returned by a debug adapter if the
         * corresponding capability `supportsVariableType` is true.
         */
        @JsonProperty("type")
        public String type;

        /**
         * Properties of an evaluate result that can be used to determine how to
         * render the result in the UI.
         */
        @JsonProperty("presentationHint")
        public DAPVariablePresentationHint presentationHint;

        /**
         * If `variablesReference` is > 0, the evaluate result is structured and its
         * children can be retrieved by passing `variablesReference` to the
         * `variables` request as long as execution remains suspended. See 'Lifetime
         * of Object References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        public int variablesReference;

        /**
         * The number of named child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("namedVariables")
        public Integer namedVariables;

        /**
         * The number of indexed child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("indexedVariables")
        public Integer indexedVariables;

        /**
         * A memory reference to a location appropriate for this result.
         * For pointer type eval results, this is generally a reference to the
         * memory address contained in the pointer.
         * This attribute should be returned by a debug adapter if corresponding
         * capability `supportsMemoryReferences` is true.
         */
        @JsonProperty("memoryReference")
        public String memoryReference;
    }
}
