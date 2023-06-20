package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPStackFrameFormat : DAPAdditionalProperties() {
    /**
     * Displays parameters for the stack frame.
     */
    @JsonProperty("parameters")
    var parameters: Boolean? = null

    /**
     * Displays the types of parameters for the stack frame.
     */
    @JsonProperty("parameterTypes")
    var parameterTypes: Boolean? = null

    /**
     * Displays the names of parameters for the stack frame.
     */
    @JsonProperty("parameterNames")
    var parameterNames: Boolean? = null

    /**
     * Displays the values of parameters for the stack frame.
     */
    @JsonProperty("parameterValues")
    var parameterValues: Boolean? = null

    /**
     * Displays the line number of the stack frame.
     */
    @JsonProperty("line")
    var line: Boolean? = null

    /**
     * Displays the module of the stack frame.
     */
    @JsonProperty("module")
    var module: Boolean? = null

    /**
     * Includes all stack frames, including those the debug adapter might
     * otherwise hide.
     */
    @JsonProperty("includeAll")
    var includeAll: Boolean? = null
}
