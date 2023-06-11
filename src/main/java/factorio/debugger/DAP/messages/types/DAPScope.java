package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPScope extends DAPAdditionalProperties {
    /**
     * Name of the scope such as 'Arguments', 'Locals', or 'Registers'. This
     * string is shown in the UI as is and can be translated.
     */
    @JsonProperty("name")
    public String name;

    /**
     * A hint for how to present this scope in the UI. If this attribute is
     * missing, the scope is shown with a generic UI.
     * Values:
     * 'arguments': Scope contains method arguments.
     * 'locals': Scope contains local variables.
     * 'registers': Scope contains registers. Only a single `registers` scope
     * should be returned from a `scopes` request.
     * etc.
     */
    // 'arguments' | 'locals' | 'registers' | string
    @JsonProperty("presentationHint")
    public String presentationHint;

    /**
     * The variables of this scope can be retrieved by passing the value of
     * `variablesReference` to the `variables` request as long as execution
     * remains suspended. See 'Lifetime of Object References' in the Overview
     * section for details.
     */
    @JsonProperty("variablesReference")
    public int variablesReference;

    /**
     * The number of named variables in this scope.
     * The client can use this information to present the variables in a paged UI
     * and fetch them in chunks.
     */
    @JsonProperty("namedVariables")
    public Integer namedVariables;

    /**
     * The number of indexed variables in this scope.
     * The client can use this information to present the variables in a paged UI
     * and fetch them in chunks.
     */
    @JsonProperty("indexedVariables")
    public Integer indexedVariables;

    /**
     * If true, the number of variables in this scope is large or expensive to
     * retrieve.
     */
    @JsonProperty("expensive")
    public boolean expensive;

    /**
     * The source for this scope.
     */
    @JsonProperty("source")
    public DAPSource source;

    /**
     * The start line of the range covered by this scope.
     */
    @JsonProperty("line")
    public Integer line;

    /**
     * Start position of the range covered by the scope. It is measured in UTF-16
     * code units and the client capability `columnsStartAt1` determines whether
     * it is 0- or 1-based.
     */
    @JsonProperty("column")
    public Integer column;

    /**
     * The end line of the range covered by this scope.
     */
    @JsonProperty("endLine")
    public Integer endLine;

    /**
     * End position of the range covered by the scope. It is measured in UTF-16
     * code units and the client capability `columnsStartAt1` determines whether
     * it is 0- or 1-based.
     */
    @JsonProperty("endColumn")
    public Integer endColumn;
}
