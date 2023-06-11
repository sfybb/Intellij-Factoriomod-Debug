package factorio.debugger.DAP.messages.requests;

import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity;

@JsonTypeName("stepIn")
public class DAPStepInRequest extends DAPRequest<DAPStepInRequest.StepInArguments> {

    public DAPStepInRequest(final int threadId) {
        this.arguments = new StepInArguments(threadId);
    }


    public DAPStepInRequest() {
        this.arguments = new StepInArguments();
    }
    public static class StepInArguments extends DAPAdditionalProperties {
        /**
         * Specifies the thread for which to resume execution for one step-into (of
         * the given granularity).
         */
        @JsonProperty("threadId")
        public int threadId;

        /**
         * If this flag is true, all other suspended threads are not resumed.
         */
        @JsonProperty("singleThread")
        public @Nullable Boolean singleThread;

        /**
         * Id of the target to step into.
         */
        @JsonProperty("targetId")
        public @Nullable Integer targetId;

        /**
         * Stepping granularity. If no granularity is specified, a granularity of
         * `statement` is assumed.
         */
        @JsonProperty("granularity")
        public @Nullable DAPSteppingGranularity granularity;

        public StepInArguments(final int threadId) {
            this.threadId = threadId;
            this.singleThread = true;
        }

        public StepInArguments() {
            this.singleThread = false;
            this.targetId = 0;
        }
    }
}
