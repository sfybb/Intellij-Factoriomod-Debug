package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("threads")
public class DAPThreadsRequest extends DAPRequest<DAPThreadsRequest.ThreadsRequestArguments> {
    public static class ThreadsRequestArguments extends DAPAdditionalProperties {

    }
}
