package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPColumnDescriptor extends DAPAdditionalProperties {
    /**
     * Name of the attribute rendered in this column.
     */
    @JsonProperty("attributeName")
    public String attributeName;

    /**
     * Header UI label of column.
     */
    @JsonProperty("label")
    public String label;

    /**
     * Format to use for the rendered values in this column. TBD how the format
     * strings looks like.
     */
    @JsonProperty("format")
    public String format;

    /**
     * Datatype of values in this column. Defaults to `string` if not specified.
     * Values: 'string', 'number', 'boolean', 'unixTimestampUTC'
     */
    @JsonProperty("type")
    public ColumnTypes type;

    enum ColumnTypes {
        @JsonProperty("string")
        STRING,
        @JsonProperty("number")
        NUMBER,
        @JsonProperty("boolean")
        BOOLEAN,
        @JsonProperty("unixTimestampUTC")
        TIMESTAMP
    }

    /**
     * Width of this column in characters (hint only).
     */
    @JsonProperty("width")
    public int width;
}
