package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPMessage : DAPAdditionalProperties() {
    /**
     * Unique (within a debug adapter implementation) identifier for the message.
     * The purpose of these error IDs is to help extension authors that have the
     * requirement that every user visible error message needs a corresponding
     * error number, so that users or customer support can find information about
     * the specific error more easily.
     */
    @JsonProperty("id")
    var id: Int = 0

    /**
     * A format string for the message. Embedded variables have the form `{name}`.
     * If variable name starts with an underscore character, the variable does not
     * contain user data (PII) and can be safely used for telemetry purposes.
     */
    @JsonProperty("format")
    lateinit var format: String

    /**
     * An object used as a dictionary for looking up the variables in the format
     * string.
     */
    @JsonProperty("variables")
    var variables: Map<String, String> = mapOf()

    /**
     * If true send to telemetry.
     */
    @JsonProperty("sendTelemetry")
    var sendTelemetry: Boolean? = null

    /**
     * If true show user.
     */
    @JsonProperty("showUser")
    var showUser: Boolean? = null

    /**
     * A url where additional information about this message can be found.
     */
    @JsonProperty("url")
    var url: String? = null

    /**
     * A label that is presented to the user as the UI for opening the url.
     */
    @JsonProperty("urlLabel")
    var urlLabel: String? = null
}
