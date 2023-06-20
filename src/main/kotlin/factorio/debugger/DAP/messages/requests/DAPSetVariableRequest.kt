package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPSetVariableRequest.SetVariableRequestArguments
import factorio.debugger.DAP.messages.types.DAPValueFormat

@JsonTypeName("setVariable")
class DAPSetVariableRequest : DAPRequest<SetVariableRequestArguments> {
    constructor(variablesReference: Int, name: String, value: String)
            : super(SetVariableRequestArguments(variablesReference, name, value))

    constructor(variablesReference: Int, name: String, value: String, format: DAPValueFormat?)
            : super(SetVariableRequestArguments(variablesReference, name, value, format))

    class SetVariableRequestArguments : DAPAdditionalProperties {
        /**
         * The reference of the variable container. The `variablesReference` must have
         * been obtained in the current suspended state. See 'Lifetime of Object
         * References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        var variablesReference: Int

        /**
         * The name of the variable in the container.
         */
        @JsonProperty("name")
        var name: String

        /**
         * The value of the variable.
         */
        @JsonProperty("value")
        var value: String

        /**
         * Specifies details on how to format the response value.
         */
        @JsonProperty("format")
        var format: DAPValueFormat? = null

        constructor(
            variablesReference: Int,
            name: String,
            value: String
        ) {
            this.variablesReference = variablesReference
            this.name = name
            this.value = value
        }

        constructor(
            variablesReference: Int,
            name: String,
            value: String,
            format: DAPValueFormat?
        ) {
            this.variablesReference = variablesReference
            this.name = name
            this.value = value
            this.format = format
        }
    }
}
