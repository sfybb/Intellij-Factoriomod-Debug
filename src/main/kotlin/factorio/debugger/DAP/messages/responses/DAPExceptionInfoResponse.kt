package factorio.debugger.DAP.messages.responses

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.types.DAPExceptionBreakMode
import factorio.debugger.DAP.messages.types.DAPExceptionDetails

@JsonTypeName("exceptionInfo")
class DAPExceptionInfoResponse : DAPResponse() {
    @JsonProperty("body")
    lateinit var body: ExceptionInfoResponseBody

    class ExceptionInfoResponseBody : DAPAdditionalProperties() {
        /**
         * ID of the exception that was thrown.
         */
        @JsonProperty("exceptionId")
        lateinit var exceptionId: String

        /**
         * Descriptive text for the exception.
         */
        @JsonProperty("description")
        var description: String? = null

        /**
         * Mode that caused the exception notification to be raised.
         */
        @JsonProperty("breakMode")
        lateinit var breakMode: DAPExceptionBreakMode

        /**
         * Detailed information about the exception.
         */
        @JsonProperty("details")
        var details: DAPExceptionDetails? = null
    }

    override fun toString(): String {
        return "${super.toString()} Exception ID '${body.exceptionId}'"
    }
}
