package factorio.debugger.DAP.messages.events

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("initialized")
class DAPInitializedEvent : DAPEvent()
