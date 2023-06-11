package factorio.debugger.DAP.messages.requests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPValueFormat;

@JsonTypeName("evaluate")
public class DAPEvaluateRequest extends DAPRequest<DAPEvaluateRequest.EvaluateArguments> {
    public DAPEvaluateRequest(@NotNull final String expression, final int frameId, @Nullable final EvalContext context) {
        this.arguments = new EvaluateArguments(expression, frameId, context);
    }
    public static class EvaluateArguments {
        /**
         * The expression to evaluate.
         */
        @JsonProperty("expression")
        public String expression;

        /**
         * Evaluate the expression in the scope of this stack frame. If not specified,
         * the expression is evaluated in the global scope.
         */
        @JsonProperty("frameId")
        public Integer frameId;

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
        public String context;

        /**
         * Specifies details on how to format the result.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportsValueFormattingOptions` is true.
         */
        @JsonProperty("format")
        public DAPValueFormat format;

        public EvaluateArguments(@NotNull final String expression, final int frameId, @Nullable final EvalContext context) {
            this.expression = expression;
            this.frameId = frameId;
            this.context = context != null ? context.name().toLowerCase() : null;
        }
    }

    public enum EvalContext {
        WATCH,
        REPL,
        HOVER,
        CLIPBOARD,
        VARIABLES;
    }
}
