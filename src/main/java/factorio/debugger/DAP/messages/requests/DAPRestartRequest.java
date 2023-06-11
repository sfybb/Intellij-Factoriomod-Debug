package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("restart")
public class DAPRestartRequest extends DAPRequest<DAPRestartRequest.RestartArguments> {
    public static class RestartArguments extends DAPAdditionalProperties {
        /**
         * The latest version of the `launch` or `attach` configuration.
         */
    }
}
