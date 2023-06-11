package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPSteppingGranularity;

@JsonTypeName("next")
public class DAPNextRequest extends DAPRequest<DAPNextRequest.NextArguments> {
    public DAPNextRequest(int threadId, DAPSteppingGranularity granularity) {
        this.arguments = new NextArguments(threadId, granularity);
    }

    public DAPNextRequest(int threadId) {
        this.arguments = new NextArguments(threadId, DAPSteppingGranularity.STATEMENT);
    }

    public DAPNextRequest(DAPSteppingGranularity granularity) {
        this.arguments = new NextArguments(granularity);
    }

    public DAPNextRequest() {
        this.arguments = new NextArguments(DAPSteppingGranularity.STATEMENT);
    }

    public static class NextArguments extends DAPAdditionalProperties {
        public NextArguments(int threadId, DAPSteppingGranularity granularity) {
            this.threadId = threadId;
            this.singleThread = true;
            this.granularity = granularity;
        }

        public NextArguments(DAPSteppingGranularity granularity) {
            this.threadId = 0;
            this.singleThread = false;
            this.granularity = granularity;
        }

        /**
         * Specifies the thread for which to resume execution for one step (of the
         * given granularity).
         */
        @JsonProperty("threadId")
        public int threadId;

        /**
         * If this flag is true, all other suspended threads are not resumed.
         */
        @JsonProperty("singleThread")
        public Boolean singleThread;

        /**
         * Stepping granularity. If no granularity is specified, a granularity of
         * `statement` is assumed.
         */
        @JsonProperty("granularity")
        public DAPSteppingGranularity granularity;
    }
}
