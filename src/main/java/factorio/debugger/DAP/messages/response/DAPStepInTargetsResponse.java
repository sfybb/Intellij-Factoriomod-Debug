package factorio.debugger.DAP.messages.response;

import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPStepInTarget;

@JsonTypeName("stepInTargets")
public class DAPStepInTargetsResponse extends DAPResponse {

    @JsonProperty("body")
    public @Nullable Body body;

    public static class Body extends DAPAdditionalProperties {
        /**
         * The possible step-in targets of the specified source location.
         */
        @JsonProperty("targets")
        public @Nullable DAPStepInTarget[] targets;
    }
}
