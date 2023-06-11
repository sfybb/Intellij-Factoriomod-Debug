package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("configurationDone")
public class DAPConfigurationDoneRequest extends DAPRequest<DAPConfigurationDoneRequest.ConfigurationDoneArguments> {
    ConfigurationDoneArguments arguments;

    public DAPConfigurationDoneRequest() {
        arguments = new ConfigurationDoneArguments();
    }

    public static class ConfigurationDoneArguments extends DAPAdditionalProperties {
    }
}
