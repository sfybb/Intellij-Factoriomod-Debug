package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPStackFrameFormat extends DAPAdditionalProperties {
    /**
     * Displays parameters for the stack frame.
     */
    @JsonProperty("parameters")
    public Boolean parameters;

    /**
     * Displays the types of parameters for the stack frame.
     */
    @JsonProperty("parameterTypes")
    public Boolean parameterTypes;

    /**
     * Displays the names of parameters for the stack frame.
     */
    @JsonProperty("parameterNames")
    public Boolean parameterNames;

    /**
     * Displays the values of parameters for the stack frame.
     */
    @JsonProperty("parameterValues")
    public Boolean parameterValues;

    /**
     * Displays the line number of the stack frame.
     */
    @JsonProperty("line")
    public Boolean line;

    /**
     * Displays the module of the stack frame.
     */
    @JsonProperty("module")
    public Boolean module;

    /**
     * Includes all stack frames, including those the debug adapter might
     * otherwise hide.
     */
    @JsonProperty("includeAll")
    public Boolean includeAll;
}
