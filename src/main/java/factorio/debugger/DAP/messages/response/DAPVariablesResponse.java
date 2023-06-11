package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPVariable;

@JsonTypeName("variables")
public class DAPVariablesResponse extends DAPResponse {
    @JsonProperty("body")
    public VariablesResponseBody body;

    public static class VariablesResponseBody extends DAPAdditionalProperties {
        /**
         * All (or a range) of variables for the given variable reference.
         */
        @JsonProperty("variables")
        public DAPVariable[] variables;
    }
}
