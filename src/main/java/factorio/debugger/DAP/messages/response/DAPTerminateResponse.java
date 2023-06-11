package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;

@JsonTypeName("terminate")
public class DAPTerminateResponse extends DAPResponse {
}
