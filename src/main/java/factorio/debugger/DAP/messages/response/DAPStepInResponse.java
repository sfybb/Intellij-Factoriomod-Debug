package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPResponse;

@JsonTypeName("stepIn")
public class DAPStepInResponse extends DAPResponse {
}
