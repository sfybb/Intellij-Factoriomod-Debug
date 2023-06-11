package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("dataBreakpointInfo")
public class DAPDataBreakpointInfoRequest extends DAPRequest<DAPDataBreakpointInfoRequest.DataBreakpointInfoArguments> {
    public static class DataBreakpointInfoArguments extends DAPAdditionalProperties {
        /**
         * Reference to the variable container if the data breakpoint is requested for
         * a child of the container. The `variablesReference` must have been obtained
         * in the current suspended state. See 'Lifetime of Object References' in the
         * Overview section for details.
         */
        @JsonProperty("variablesReference")
        public Integer variablesReference;

        /**
         * The name of the variable's child to obtain data breakpoint information for.
         * If `variablesReference` isn't specified, this can be an expression.
         */
        @JsonProperty("name")
        public String name;

        /**
         * When `name` is an expression, evaluate it in the scope of this stack frame.
         * If not specified, the expression is evaluated in the global scope. When
         * `variablesReference` is specified, this property has no effect.
         */
        @JsonProperty("frameId")
        public Integer frameId;
    }
}
