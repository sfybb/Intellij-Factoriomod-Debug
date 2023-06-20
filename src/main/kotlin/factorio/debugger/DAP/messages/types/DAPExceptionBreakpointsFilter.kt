package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPExceptionBreakpointsFilter : DAPAdditionalProperties() {
    /**
     * The internal ID of the filter option. This value is passed to the
     * `setExceptionBreakpoints` request.
     */
    @JsonProperty("filter")
    lateinit var filter: String

    /**
     * The name of the filter option. This is shown in the UI.
     */
    @JsonProperty("label")
    lateinit var label: String

    /**
     * A help text providing additional information about the exception filter.
     * This string is typically shown as a hover and can be translated.
     */
    @JsonProperty("description")
    var description: String? = null

    /**
     * Initial value of the filter option. If not specified a value false is
     * assumed.
     */
    @JsonProperty("default")
    var isDefault = false

    /**
     * Controls whether a condition can be specified for this filter option. If
     * false or missing, a condition can not be set.
     */
    @JsonProperty("supportsCondition")
    var supportsCondition = false

    /**
     * A help text providing information about the condition. This string is shown
     * as the placeholder text for a text box and can be translated.
     */
    @JsonProperty("conditionDescription")
    var conditionDescription: String? = null
}
