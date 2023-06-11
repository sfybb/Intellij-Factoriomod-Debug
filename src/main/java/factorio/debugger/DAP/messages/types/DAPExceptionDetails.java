package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPExceptionDetails extends DAPAdditionalProperties {
    /**
     * Message contained in the exception.
     */
    @JsonProperty("message")
    public String message;

    /**
     * Short type name of the exception object.
     */
    @JsonProperty("typeName")
    public String typeName;

    /**
     * Fully-qualified type name of the exception object.
     */
    @JsonProperty("fullTypeName")
    public String fullTypeName;

    /**
     * An expression that can be evaluated in the current scope to obtain the
     * exception object.
     */
    @JsonProperty("evaluateName")
    public String evaluateName;

    /**
     * Stack trace at the time the exception was thrown.
     */
    @JsonProperty("stackTrace")
    public String stackTrace;

    /**
     * Details of the exception contained by this exception, if any.
     */
    @JsonProperty("innerException")
    public DAPExceptionDetails[] innerException;
}
