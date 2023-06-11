package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;

@JsonTypeName("pause")
public class DAPPauseResponse extends DAPResponse {
    @Override
    public String toString() {
        return "Response: pause";
    }
}
