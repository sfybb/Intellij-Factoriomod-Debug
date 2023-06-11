package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPValueFormat;

@JsonTypeName("variables")
public class DAPVariablesRequest extends DAPRequest<DAPVariablesRequest.VariablesArguments> {
    public DAPVariablesRequest(int varRef) {
        this.arguments = new VariablesArguments(varRef);
    }

    public DAPVariablesRequest(int varRef, int offset, int count) {
        this.arguments = new VariablesArguments(varRef, offset, count);
    }

    public static class VariablesArguments extends DAPAdditionalProperties {
        public VariablesArguments(int varRef) {
            this.variablesReference = varRef;
        }

        public VariablesArguments(int varRef, int offset, int count) {
            this.variablesReference = varRef;
            this.start = offset;
            this.count = count;
        }
        /**
         * The variable for which to retrieve its children. The `variablesReference`
         * must have been obtained in the current suspended state. See 'Lifetime of
         * Object References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        public int variablesReference;

        /**
         * Filter to limit the child variables to either named or indexed. If omitted,
         * both types are fetched.
         * Values: 'indexed', 'named'
         */
        @JsonProperty("filter")
        public Filter filter;
        public enum Filter {
            @JsonProperty("indexed")
            INDEXED,
            @JsonProperty("named")
            NAMED
        }

        /**
         * The index of the first variable to return; if omitted children start at 0.
         */
        @JsonProperty("start")
        public Integer start;

        /**
         * The number of variables to return. If count is missing or 0, all variables
         * are returned.
         */
        @JsonProperty("count")
        public Integer count;

        /**
         * Specifies details on how to format the Variable values.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        public DAPValueFormat format;
    }
}
