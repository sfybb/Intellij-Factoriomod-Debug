package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPThreadsRequest.ThreadsRequestArguments

@JsonTypeName("threads")
class DAPThreadsRequest : DAPRequest<ThreadsRequestArguments>(ThreadsRequestArguments()) {
    class ThreadsRequestArguments : DAPAdditionalProperties()
}
