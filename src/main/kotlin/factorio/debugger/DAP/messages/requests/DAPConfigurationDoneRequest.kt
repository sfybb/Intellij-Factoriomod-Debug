package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPConfigurationDoneRequest.ConfigurationDoneArguments

@JsonTypeName("configurationDone")
class DAPConfigurationDoneRequest : DAPRequest<ConfigurationDoneArguments>(ConfigurationDoneArguments()) {

    class ConfigurationDoneArguments : DAPAdditionalProperties()
}
