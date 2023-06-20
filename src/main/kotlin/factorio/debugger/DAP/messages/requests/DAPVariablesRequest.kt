package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPVariablesRequest.VariablesArguments
import factorio.debugger.DAP.messages.types.DAPValueFormat

@JsonTypeName("variables")
class DAPVariablesRequest : DAPRequest<VariablesArguments> {
    constructor(varRef: Int) : super(VariablesArguments(varRef))

    constructor(varRef: Int, offset: Int, count: Int) : super(VariablesArguments(varRef, offset, count))

    class VariablesArguments : DAPAdditionalProperties {
        constructor(varRef: Int) {
            variablesReference = varRef
        }

        constructor(varRef: Int, offset: Int, count: Int) {
            variablesReference = varRef
            start = offset
            this.count = count
        }

        /**
         * The variable for which to retrieve its children. The `variablesReference`
         * must have been obtained in the current suspended state. See 'Lifetime of
         * Object References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        var variablesReference: Int

        /**
         * Filter to limit the child variables to either named or indexed. If omitted,
         * both types are fetched.
         * Values: 'indexed', 'named'
         */
        @JsonProperty("filter")
        var filter: Filter? = null

        /**
         * The index of the first variable to return; if omitted children start at 0.
         */
        @JsonProperty("start")
        var start: Int? = null

        /**
         * The number of variables to return. If count is missing or 0, all variables
         * are returned.
         */
        @JsonProperty("count")
        var count: Int? = null

        /**
         * Specifies details on how to format the Variable values.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        var format: DAPValueFormat? = null
    }

    enum class Filter {
        @JsonProperty("indexed")
        INDEXED,
        @JsonProperty("named")
        NAMED
    }
}
