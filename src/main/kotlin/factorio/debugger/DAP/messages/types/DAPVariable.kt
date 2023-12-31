package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPVariable : DAPAdditionalProperties() {
    /**
     * The variable's name.
     */
    @JsonProperty("name")
    lateinit var name: String

    /**
     * The variable's value.
     * This can be a multi-line text, e.g. for a function the body of a function.
     * For structured variables (which do not have a simple value), it is
     * recommended to provide a one-line representation of the structured object.
     * This helps to identify the structured object in the collapsed state when
     * its children are not yet visible.
     * An empty string can be used if no value should be shown in the UI.
     */
    @JsonProperty("value")
    lateinit var value: String

    /**
     * The type of the variable's value. Typically shown in the UI when hovering
     * over the value.
     * This attribute should only be returned by a debug adapter if the
     * corresponding capability `supportsVariableType` is true.
     */
    @JsonProperty("type")
    var type: String? = null

    /**
     * Properties of a variable that can be used to determine how to render the
     * variable in the UI.
     */
    @JsonProperty("presentationHint")
    var presentationHint: DAPVariablePresentationHint? = null

    /**
     * The evaluatable name of this variable which can be passed to the `evaluate`
     * request to fetch the variable's value.
     */
    @JsonProperty("evaluateName")
    var evaluateName: String? = null

    /**
     * If `variablesReference` is > 0, the variable is structured and its children
     * can be retrieved by passing `variablesReference` to the `variables` request
     * as long as execution remains suspended. See 'Lifetime of Object References'
     * in the Overview section for details.
     */
    @JsonProperty("variablesReference")
    var variablesReference: Int = 0

    /**
     * The number of named child variables.
     * The client can use this information to present the children in a paged UI
     * and fetch them in chunks.
     */
    @JsonProperty("namedVariables")
    var namedVariables: Int? = null

    /**
     * The number of indexed child variables.
     * The client can use this information to present the children in a paged UI
     * and fetch them in chunks.
     */
    @JsonProperty("indexedVariables")
    var indexedVariables: Int? = null

    /**
     * The memory reference for the variable if the variable represents executable
     * code, such as a function pointer.
     * This attribute is only required if the corresponding capability
     * `supportsMemoryReferences` is true.
     */
    @JsonProperty("memoryReference")
    var memoryReference: String? = null
}
