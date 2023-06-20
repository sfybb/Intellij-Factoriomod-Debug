package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("cancel")
class DAPCancelResponse : DAPResponse()
