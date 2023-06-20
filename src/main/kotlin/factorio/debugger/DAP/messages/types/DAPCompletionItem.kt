package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPCompletionItem extends DAPAdditionalProperties {
    /**
     * The label of this completion item. By default this is also the text that is
     * inserted when selecting this completion.
     */
    @JsonProperty("label")
    public String label;

    /**
     * If text is returned and not an empty string, then it is inserted instead of
     * the label.
     */
    @JsonProperty("text")
    public String text;

    /**
     * A string that should be used when comparing this item with other items. If
     * not returned or an empty string, the `label` is used instead.
     */
    @JsonProperty("sortText")
    public String sortText;

    /**
     * A human-readable string with additional information about this item, like
     * type or symbol information.
     */
    @JsonProperty("detail")
    public String detail;

    /**
     * The item's type. Typically the client uses this information to render the
     * item in the UI with an icon.
     */
    @JsonProperty("type")
    public DAPCompletionItemType type;

    /**
     * Start position (within the `text` attribute of the `completions` request)
     * where the completion text is added. The position is measured in UTF-16 code
     * units and the client capability `columnsStartAt1` determines whether it is
     * 0- or 1-based. If the start position is omitted the text is added at the
     * location specified by the `column` attribute of the `completions` request.
     */
    @JsonProperty("start")
    public Integer start;

    /**
     * Length determines how many characters are overwritten by the completion
     * text and it is measured in UTF-16 code units. If missing the value 0 is
     * assumed which results in the completion text being inserted.
     */
    @JsonProperty("length")
    public Integer length;

    /**
     * Determines the start of the new selection after the text has been inserted
     * (or replaced). `selectionStart` is measured in UTF-16 code units and must
     * be in the range 0 and length of the completion text. If omitted the
     * selection starts at the end of the completion text.
     */
    @JsonProperty("selectionStart")
    public Integer selectionStart;

    /**
     * Determines the length of the new selection after the text has been inserted
     * (or replaced) and it is measured in UTF-16 code units. The selection can
     * not extend beyond the bounds of the completion text. If omitted the length
     * is assumed to be 0.
     */
    @JsonProperty("selectionLength")
    public Integer selectionLength;
}
