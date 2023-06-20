package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPScope;

@JsonTypeName("scopes")
public class DAPScopesResponse extends DAPResponse {
    @JsonProperty("body")
    public ScopesResponseBody body;
    public static class ScopesResponseBody extends DAPAdditionalProperties {
        /**
         * The scopes of the stack frame. If the array has length zero, there are no
         * scopes available.
         */
        @JsonProperty("scopes")
        public DAPScope[] scopes;
    }

    @Override
    public String toString() {
        return super.toString() + ": #scopes: "+ body.scopes.length;
    }
}
