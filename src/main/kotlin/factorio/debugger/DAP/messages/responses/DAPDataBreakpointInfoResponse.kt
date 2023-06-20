package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPDataBreakpointAccessType

@JsonTypeName("dataBreakpointInfo")
class DAPDataBreakpointInfoResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: DataBreakpointInfoResponseBody

    class DataBreakpointInfoResponseBody : DAPAdditionalProperties() {
        /**
         * An identifier for the data on which a data breakpoint can be registered
         * with the `setDataBreakpoints` request or null if no data breakpoint is
         * available.
         */
        @JsonProperty("dataId")
        var dataId: String? = null

        /**
         * UI string that describes on what data the breakpoint is set on or why a
         * data breakpoint is not available.
         */
        @JsonProperty("description")
        lateinit var description: String

        /**
         * Attribute lists the available access types for a potential data
         * breakpoint. A UI client could surface this information.
         */
        @JsonProperty("accessTypes")
        var accessTypes: Array<DAPDataBreakpointAccessType>? = null

        /**
         * Attribute indicates that a potential data breakpoint could be persisted
         * across sessions.
         */
        @JsonProperty("canPersist")
        var canPersist: Boolean? = null
    }

    override fun toString(): String {
        return "${super.toString()} Description '${body.description}'"
    }
}
