package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

/*
    currentpc: https://lua-api.factorio.com/latest/Libraries.html#%E2%80%A2-debug.getinfo()
 */
public class DAPStackFrame extends DAPAdditionalProperties {
    /**
     * An identifier for the stack frame. It must be unique across all threads.
     * This id can be used to retrieve the scopes of the frame with the `scopes`
     * request or to restart the execution of a stack frame.
     */
    @JsonProperty("id")
    public Integer id;

    /**
     * The name of the stack frame, typically a method name.
     */
    @JsonProperty("name")
    public String name;

    /**
     * The source of the frame.
     */
    @JsonProperty("source")
    public DAPSource source;

    /**
     * The line within the source of the frame. If the source attribute is missing
     * or doesn't exist, `line` is 0 and should be ignored by the client.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * Start position of the range covered by the stack frame. It is measured in
     * UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based. If attribute `source` is missing or doesn't
     * exist, `column` is 0 and should be ignored by the client.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the range covered by the stack frame.
     */
    @JsonProperty("body")
    public Integer endLine;

    /**
     * End position of the range covered by the stack frame. It is measured in
     * UTF-16 code units and the client capability `columnsStartAt1` determines
     * whether it is 0- or 1-based.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;

    /**
     * Indicates whether this frame can be restarted with the `restart` request.
     * Clients should only use this if the debug adapter supports the `restart`
     * request and the corresponding capability `supportsRestartRequest` is true.
     * If a debug adapter has this capability, then `canRestart` defaults to
     * `true` if the property is absent.
     */
    @JsonProperty("canRestart")
    public Boolean canRestart;

    /**
     * A memory reference for the current instruction pointer in this frame.
     */
    @JsonProperty("instructionPointerReference")
    public String instructionPointerReference;

    /**
     * The module associated with this frame, if any.
     */
    @JsonProperty("moduleId")
    @JsonRawValue
    public String moduleId /*?: number | string*/;

    /**
     * A hint for how to present this frame in the UI.
     * A value of `label` can be used to indicate that the frame is an artificial
     * frame that is used as a visual label or separator. A value of `subtle` can
     * be used to change the appearance of a frame in a 'subtle' way.
     * Values: 'normal', 'label', 'subtle'
     */
    @JsonProperty("presentationHint")
    public PersentationHint presentationHint;

    public enum PersentationHint {
        @JsonProperty("normal")
        NORMAL,
        @JsonProperty("label")
        LABEL,
        @JsonProperty("subtle")
        SUBTLE
    }
}
