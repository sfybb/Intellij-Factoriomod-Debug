package factorio.debugger.DAP.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPResponse;
import factorio.debugger.DAP.messages.types.DAPExceptionBreakMode;
import factorio.debugger.DAP.messages.types.DAPExceptionDetails;

@JsonTypeName("exceptionInfo")
public class DAPExceptionInfoResponse extends DAPResponse {
    @JsonProperty("body")
    public ExceptionInfoResponseBody body;
    public static class ExceptionInfoResponseBody extends DAPAdditionalProperties {
        /**
         * ID of the exception that was thrown.
         */
        @JsonProperty("exceptionId")
        public String exceptionId;

        /**
         * Descriptive text for the exception.
         */
        @JsonProperty("description")
        public String description;

        /**
         * Mode that caused the exception notification to be raised.
         */
        @JsonProperty("breakMode")
        public DAPExceptionBreakMode breakMode;

        /**
         * Detailed information about the exception.
         */
        @JsonProperty("details")
        public DAPExceptionDetails details;
    }
}
