package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPRestartRequest.RestartArguments

@JsonTypeName("restart")
class DAPRestartRequest : DAPRequest<RestartArguments?>(RestartArguments()) {
    class RestartArguments : DAPAdditionalProperties() {
        /**
         * The latest version of the `launch` or `attach` configuration.
         */
        val arguments: DAPLaunchRequest.LaunchRequestArguments? = null
    }
}
