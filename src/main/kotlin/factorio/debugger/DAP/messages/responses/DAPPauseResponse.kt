package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("pause")
class DAPPauseResponse : DAPResponse()
