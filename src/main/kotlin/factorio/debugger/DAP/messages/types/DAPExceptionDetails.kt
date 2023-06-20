package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPExceptionDetails : DAPAdditionalProperties() {
    /**
     * Message contained in the exception.
     */
    @JsonProperty("message")
    var message: String? = null

    /**
     * Short type name of the exception object.
     */
    @JsonProperty("typeName")
    var typeName: String? = null

    /**
     * Fully-qualified type name of the exception object.
     */
    @JsonProperty("fullTypeName")
    var fullTypeName: String? = null

    /**
     * An expression that can be evaluated in the current scope to obtain the
     * exception object.
     */
    @JsonProperty("evaluateName")
    var evaluateName: String? = null

    /**
     * Stack trace at the time the exception was thrown.
     */
    @JsonProperty("stackTrace")
    var stackTrace: String? = null

    /**
     * Details of the exception contained by this exception, if any.
     */
    @JsonProperty("innerException")
    var innerException: Array<DAPExceptionDetails> = arrayOf()
}
