package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPVariable
import factorio.debugger.DAP.messages.types.DAPVariablePresentationHint

@JsonTypeName("setExpression")
class DAPSetExpressionResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: SetExpressionResponseBody

    class SetExpressionResponseBody : DAPAdditionalProperties() {
        /**
         * The new value of the expression.
         */
        @JsonProperty("value")
        lateinit var value: String

        /**
         * The type of the value.
         * This attribute should only be returned by a debug adapter if the
         * corresponding capability `supportsVariableType` is true.
         */
        @JsonProperty("type")
        var type: String? = null

        /**
         * Properties of a value that can be used to determine how to render the
         * result in the UI.
         */
        @JsonProperty("presentationHint")
        var presentationHint: DAPVariablePresentationHint? = null

        /**
         * If `variablesReference` is > 0, the evaluate result is structured and its
         * children can be retrieved by passing `variablesReference` to the
         * `variables` request as long as execution remains suspended. See 'Lifetime
         * of Object References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        var variablesReference: Int? = null

        /**
         * The number of named child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("namedVariables")
        var namedVariables: Int? = null

        /**
         * The number of indexed child variables.
         * The client can use this information to present the variables in a paged
         * UI and fetch them in chunks.
         * The value should be less than or equal to 2147483647 (2^31-1).
         */
        @JsonProperty("indexedVariables")
        var indexedVariables: Int? = null
        fun toVariable(): DAPVariable {
            val res = DAPVariable()
            res.value = value
            res.type = type
            res.presentationHint = presentationHint
            res.variablesReference = variablesReference ?: 0
            res.namedVariables = namedVariables
            res.indexedVariables = indexedVariables
            res.memoryReference = null
            return res
        }
    }
}
