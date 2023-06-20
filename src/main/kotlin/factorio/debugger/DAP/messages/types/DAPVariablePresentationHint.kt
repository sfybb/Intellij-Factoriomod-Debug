package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPVariablePresentationHint extends DAPAdditionalProperties {
    /**
     * The kind of variable. Before introducing additional values, try to use the
     * listed values.
     * Values:
     * 'property': Indicates that the object is a property.
     * 'method': Indicates that the object is a method.
     * 'class': Indicates that the object is a class.
     * 'data': Indicates that the object is data.
     * 'event': Indicates that the object is an event.
     * 'baseClass': Indicates that the object is a base class.
     * 'innerClass': Indicates that the object is an inner class.
     * 'interface': Indicates that the object is an interface.
     * 'mostDerivedClass': Indicates that the object is the most derived class.
     * 'virtual': Indicates that the object is virtual, that means it is a
     * synthetic object introduced by the adapter for rendering purposes, e.g. an
     * index range for large arrays.
     * 'dataBreakpoint': Deprecated: Indicates that a data breakpoint is
     * registered for the object. The `hasDataBreakpoint` attribute should
     * generally be used instead.
     * etc.
     */
    // 'property' | 'method' | 'class' | 'data' | 'event' | 'baseClass'
    //        | 'innerClass' | 'interface' | 'mostDerivedClass' | 'virtual'
    //        | 'dataBreakpoint' | string
    @JsonProperty("kind")
    public String kind;

    /**
     * Set of attributes represented as an array of strings. Before introducing
     * additional values, try to use the listed values.
     * Values:
     * 'static': Indicates that the object is static.
     * 'constant': Indicates that the object is a constant.
     * 'readOnly': Indicates that the object is read only.
     * 'rawString': Indicates that the object is a raw string.
     * 'hasObjectId': Indicates that the object can have an Object ID created for
     * it.
     * 'canHaveObjectId': Indicates that the object has an Object ID associated
     * with it.
     * 'hasSideEffects': Indicates that the evaluation had side effects.
     * 'hasDataBreakpoint': Indicates that the object has its value tracked by a
     * data breakpoint.
     * etc.
     */
    // ('static' | 'constant' | 'readOnly' | 'rawString' | 'hasObjectId'
    //        | 'canHaveObjectId' | 'hasSideEffects' | 'hasDataBreakpoint' | string)[]
    @JsonProperty("attributes")
    public String[] attributes;

    /**
     * Visibility of variable. Before introducing additional values, try to use
     * the listed values.
     * Values: 'public', 'private', 'protected', 'internal', 'final', etc.
     */
    //  'public' | 'private' | 'protected' | 'internal' | 'final' | string
    @JsonProperty("visibility")
    public String visibility;

    /**
     * If true, clients can present the variable with a UI that supports a
     * specific gesture to trigger its evaluation.
     * This mechanism can be used for properties that require executing code when
     * retrieving their value and where the code execution can be expensive and/or
     * produce side-effects. A typical example are properties based on a getter
     * function.
     * Please note that in addition to the `lazy` flag, the variable's
     * `variablesReference` is expected to refer to a variable that will provide
     * the value through another `variable` request.
     */
    @JsonProperty("lazy")
    public Boolean lazy;
}
