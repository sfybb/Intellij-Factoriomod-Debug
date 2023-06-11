package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("stepInTargets")
public class DAPStepInTargetsRequest extends DAPRequest<DAPStepInTargetsRequest.Arguments> {
    public DAPStepInTargetsRequest(final int frameId) {
        this.arguments = new Arguments(frameId);
    }

    public static class Arguments {
        /**
         * The stack frame for which to retrieve the possible step-in targets.
         */
        @JsonProperty("frameId")
        public int frameId;

        public Arguments(final int frameId) {
            this.frameId = frameId;
        }
    }
}
