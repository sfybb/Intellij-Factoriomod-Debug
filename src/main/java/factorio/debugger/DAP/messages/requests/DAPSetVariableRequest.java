package factorio.debugger.DAP.messages.requests;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPValueFormat;

@JsonTypeName("setVariable")
public class DAPSetVariableRequest extends DAPRequest<DAPSetVariableRequest.Arguments> {
    public DAPSetVariableRequest(final int variablesReference,
                                 @NotNull final String name,
                                 @NotNull final String value) {
        this.arguments = new Arguments(variablesReference, name, value);
    }

    public DAPSetVariableRequest(final int variablesReference,
                                 @NotNull final String name,
                                 @NotNull final String value,
                                 @Nullable final DAPValueFormat format) {
        this.arguments = new Arguments(variablesReference, name, value, format);
    }

    public static class Arguments {
        /**
         * The reference of the variable container. The `variablesReference` must have
         * been obtained in the current suspended state. See 'Lifetime of Object
         * References' in the Overview section for details.
         */
        @JsonProperty("variablesReference")
        public int variablesReference;

        /**
         * The name of the variable in the container.
         */
        @JsonProperty("name")
        public @NotNull String name;

        /**
         * The value of the variable.
         */
        @JsonProperty("value")
        public @NotNull String value;

        /**
         * Specifies details on how to format the response value.
         */
        @JsonProperty("format")
        public @Nullable DAPValueFormat format;

        public Arguments(final int variablesReference,
                         @NotNull final String name,
                         @NotNull final String value) {
            this.variablesReference = variablesReference;
            this.name = name;
            this.value = value;
        }

        public Arguments(final int variablesReference,
                         @NotNull final String name,
                         @NotNull final String value,
                         @Nullable final DAPValueFormat format) {
            this.variablesReference = variablesReference;
            this.name = name;
            this.value = value;
            this.format = format;
        }
    }
}
