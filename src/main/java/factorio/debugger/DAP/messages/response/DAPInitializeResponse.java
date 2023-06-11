package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPCapabilities;

@JsonTypeName("initialize")
public class DAPInitializeResponse extends DAPResponse {
    @JsonProperty("body")
    public DAPCapabilities body;

    @Override
    public String toString() {
        return "Response: initialized";
    }
}
