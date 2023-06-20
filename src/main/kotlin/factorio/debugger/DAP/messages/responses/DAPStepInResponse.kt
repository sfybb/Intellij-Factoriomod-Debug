package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("stepIn")
class DAPStepInResponse : DAPResponse()
