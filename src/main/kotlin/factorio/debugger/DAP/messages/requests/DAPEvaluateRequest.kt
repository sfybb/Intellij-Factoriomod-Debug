package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest.EvaluateArguments
import factorio.debugger.DAP.messages.types.DAPValueFormat
import java.util.*

@JsonTypeName("evaluate")
class DAPEvaluateRequest(expression: String, frameId: Int, context: EvalContext?)
    : DAPRequest<EvaluateArguments>(EvaluateArguments(expression, frameId, context)) {

    class EvaluateArguments(
        /**
         * The expression to evaluate.
         */
        @field:JsonProperty("expression") var expression: String,
        /**
         * Evaluate the expression in the scope of this stack frame. If not specified,
         * the expression is evaluated in the global scope.
         */
        @field:JsonProperty(
            "frameId"
        ) var frameId: Int?, context: EvalContext?
    ) : DAPAdditionalProperties() {
        /**
         * The context in which the evaluate request is used.
         * Values:
         * 'watch': evaluate is called from a watch view context.
         * 'repl': evaluate is called from a REPL context.
         * 'hover': evaluate is called to generate the debug hover contents.
         * This value should only be used if the corresponding capability
         * `supportsEvaluateForHovers` is true.
         * 'clipboard': evaluate is called to generate clipboard contents.
         * This value should only be used if the corresponding capability
         * `supportsClipboardContext` is true.
         * 'variables': evaluate is called from a variables view context.
         * etc.
         */
        //'watch' | 'repl' | 'hover' | 'clipboard' | 'variables'
        @JsonProperty("context")
        var context: String?

        /**
         * Specifies details on how to format the result.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        var format: DAPValueFormat? = null

        init {
            this.context = context?.name?.lowercase(Locale.getDefault())
        }
    }

    enum class EvalContext {
        WATCH,
        REPL,
        HOVER,
        CLIPBOARD,
        VARIABLES
    }
}
