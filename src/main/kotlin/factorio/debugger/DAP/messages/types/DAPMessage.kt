package factorio.debugger.DAP.messages.types;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPMessage extends DAPAdditionalProperties {
    public DAPMessage() {
        variables = new HashMap<>();
    }
    /**
     * Unique (within a debug adapter implementation) identifier for the message.
     * The purpose of these error IDs is to help extension authors that have the
     * requirement that every user visible error message needs a corresponding
     * error number, so that users or customer support can find information about
     * the specific error more easily.
     */
    @JsonProperty("id")
    public Integer id;

    /**
     * A format string for the message. Embedded variables have the form `{name}`.
     * If variable name starts with an underscore character, the variable does not
     * contain user data (PII) and can be safely used for telemetry purposes.
     */
    @JsonProperty("format")
    public String format;

    /**
     * An object used as a dictionary for looking up the variables in the format
     * string.
     */
    @JsonProperty("variables")
    public Map<String, String> variables;

    /**
     * If true send to telemetry.
     */
    @JsonProperty("sendTelemetry")
    public Boolean sendTelemetry;

    /**
     * If true show user.
     */
    @JsonProperty("showUser")
    public Boolean showUser;

    /**
     * A url where additional information about this message can be found.
     */
    @JsonProperty("url")
    public String url;

    /**
     * A label that is presented to the user as the UI for opening the url.
     */
    @JsonProperty("urlLabel")
    public String urlLabel;
}
