package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.types.DAPVariable

@JsonTypeName("setVariable")
class DAPSetVariableResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: SetVariableResponseBody

    class SetVariableResponseBody {
        /**
         * The new value of the variable.
         */
        @JsonProperty("value")
        lateinit var value: String

        /**
         * The type of the new value. Typically shown in the UI when hovering over
         * the value.
         */
        @JsonProperty("type")
        var type: String? = null

        /**
         * If `variablesReference` is > 0, the new value is structured and its
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
            res.presentationHint = null
            res.variablesReference = variablesReference ?: 0
            res.namedVariables = namedVariables
            res.indexedVariables = indexedVariables
            res.memoryReference = null
            return res
        }
    }
}
