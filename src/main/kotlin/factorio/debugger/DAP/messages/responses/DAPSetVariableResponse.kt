package factorio.debugger.DAP.messages.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPVariable;

@JsonTypeName("setVariable")
public class DAPSetVariableResponse extends DAPResponse {

    @JsonProperty("body")
    public Body body;
    public static class Body {
        /**
         * The new value of the variable.
         */
        @JsonProperty("value")
        public @NotNull String value;

        /**
         * The type of the new value. Typically shown in the UI when hovering over
         * the value.
         */
        @JsonProperty("type")
        public @Nullable String type;

        /**
         * If `variablesReference` is > 0, the new value is structured and its
         * children can be retrieved by passing `variablesReference` to the
         * `variables` request as long as execution remains suspended. See 'Lifetime
         * of Object References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        public @Nullable Integer variablesReference;

        /**
         * The number of named child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("namedVariables")
        public @Nullable Integer namedVariables;

        /**
         * The number of indexed child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("indexedVariables")
        public @Nullable Integer indexedVariables;

        public DAPVariable toVariable() {
            DAPVariable res = new DAPVariable();
            res = new DAPVariable();
            res.value = value;
            res.type = type;
            res.presentationHint = null;
            res.variablesReference = variablesReference != null ? variablesReference : 0;
            res.namedVariables = namedVariables;
            res.indexedVariables = indexedVariables;
            res.memoryReference = null;
            return res;
        }
    }
}
