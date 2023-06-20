package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPColumnDescriptor : DAPAdditionalProperties() {
    /**
     * Name of the attribute rendered in this column.
     */
    @JsonProperty("attributeName")
    lateinit var attributeName: String

    /**
     * Header UI label of column.
     */
    @JsonProperty("label")
    lateinit var label: String

    /**
     * Format to use for the rendered values in this column. TBD how the format
     * strings looks like.
     */
    @JsonProperty("format")
    var format: String? = null

    /**
     * Datatype of values in this column. Defaults to `string` if not specified.
     * Values: 'string', 'number', 'boolean', 'unixTimestampUTC'
     */
    @JsonProperty("type")
    var type: ColumnTypes? = null

    enum class ColumnTypes {
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
    var width = 0
}
