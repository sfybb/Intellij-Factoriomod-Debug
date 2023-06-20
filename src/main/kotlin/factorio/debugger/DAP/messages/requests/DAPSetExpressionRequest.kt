package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPSetExpressionRequest.SetExpressionRequestArguments
import factorio.debugger.DAP.messages.types.DAPValueFormat

@JsonTypeName("setExpression")
class DAPSetExpressionRequest : DAPRequest<SetExpressionRequestArguments> {
    constructor(expression: String, value: String, frameId: Int?)
            : super(SetExpressionRequestArguments(expression, value, frameId))

    constructor(expression: String, value: String, frameId: Int?, format: DAPValueFormat?)
            : super(SetExpressionRequestArguments(expression, value, frameId, format))

    class SetExpressionRequestArguments : DAPAdditionalProperties {
        /**
         * The l-value expression to assign to.
         */
        @JsonProperty("expression")
        var expression: String

        /**
         * The value expression to assign to the l-value expression.
         */
        @JsonProperty("value")
        var value: String

        /**
         * Evaluate the expressions in the scope of this stack frame. If not
         * specified, the expressions are evaluated in the global scope.
         */
        @JsonProperty("frameId")
        var frameId: Int?

        /**
         * Specifies how the resulting value should be formatted.
         */
        @JsonProperty("format")
        var format: DAPValueFormat? = null

        constructor(expression: String, value: String, frameId: Int?) {
            this.expression = expression
            this.value = value
            this.frameId = frameId
        }

        constructor(
            expression: String, value: String,
            frameId: Int?, format: DAPValueFormat?
        ) {
            this.expression = expression
            this.value = value
            this.frameId = frameId
            this.format = format
        }
    }
}
