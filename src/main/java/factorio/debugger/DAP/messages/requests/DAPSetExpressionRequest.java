package factorio.debugger.DAP.messages.requests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPValueFormat;

@JsonTypeName("setExpression")
public class DAPSetExpressionRequest extends DAPRequest<DAPSetExpressionRequest.Arguments> {

    public DAPSetExpressionRequest(@NotNull final String expression, @NotNull final String value, @Nullable final Integer frameId) {
        this.arguments = new Arguments(expression, value, frameId);
    }

    public DAPSetExpressionRequest(@NotNull  final String expression, @NotNull  final String value,
                                   @Nullable final Integer frameId,   @Nullable final DAPValueFormat format) {
        this.arguments = new Arguments(expression, value, frameId, format);
    }
    public static class Arguments {
        /**
         * The l-value expression to assign to.
         */
        @JsonProperty("expression")
        public String expression;

        /**
         * The value expression to assign to the l-value expression.
         */
        @JsonProperty("value")
        public String value;

        /**
         * Evaluate the expressions in the scope of this stack frame. If not
         * specified, the expressions are evaluated in the global scope.
         */
        @JsonProperty("frameId")
        public Integer frameId;

        /**
         * Specifies how the resulting value should be formatted.
         */
        @JsonProperty("format")
        public DAPValueFormat format;

        public Arguments(@NotNull final String expression, @NotNull final String value, @Nullable final Integer frameId) {
            this.expression = expression;
            this.value = value;
            this.frameId = frameId;
        }

        public Arguments(@NotNull  final String expression, @NotNull  final String value,
                         @Nullable final Integer frameId,   @Nullable final DAPValueFormat format) {
            this.expression = expression;
            this.value = value;
            this.frameId = frameId;
            this.format = format;
        }
    }
}
