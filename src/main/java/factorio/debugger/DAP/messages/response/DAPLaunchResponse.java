package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;

@JsonTypeName("launch")
public class DAPLaunchResponse extends DAPResponse {

    @Override
    public String toString() {
        return "Response: launch";
    }
}
