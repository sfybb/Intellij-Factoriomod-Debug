package factorio.debugger.DAP.messages.requests;

import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("cancel")
public class DAPCancelRequest extends DAPRequest<DAPCancelRequest.Arguments> {
    public DAPCancelRequest(@Nullable final Integer requestId) {
        this.arguments = new Arguments(requestId);
    }

    public DAPCancelRequest(@Nullable final String progressId) {
        this.arguments = new Arguments(progressId);
    }

    public DAPCancelRequest(@Nullable final Integer requestId, @Nullable final String progressId) {
        this.arguments = new Arguments(requestId, progressId);
    }

    public static class Arguments {
        /**
         * The ID (attribute `seq`) of the request to cancel. If missing no request is
         * cancelled.
         * Both a `requestId` and a `progressId` can be specified in one request.
         */
        @JsonProperty("requestId")
        public @Nullable Integer requestId;

        /**
         * The ID (attribute `progressId`) of the progress to cancel. If missing no
         * progress is cancelled.
         * Both a `requestId` and a `progressId` can be specified in one request.
         */
        @JsonProperty("progressId")
        public @Nullable String progressId;

        public Arguments(@Nullable final Integer requestId) {
            this(requestId, null);
        }

        public Arguments(@Nullable final String progressId) {
            this(null, progressId);
        }

        public Arguments(@Nullable final Integer requestId, @Nullable final String progressId) {
            this.requestId = requestId;
            this.progressId = progressId;
        }
    }
}
