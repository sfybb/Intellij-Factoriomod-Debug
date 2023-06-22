package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPModule : DAPAdditionalProperties() {
    /**
     * Unique identifier for the module.
     */
    @JsonProperty("id")
    @JsonRawValue
    lateinit var id: String

    /**
     * A name of the module.
     */
    @JsonProperty("name")
    lateinit var name: String

    /**
     * Logical full path to the module. The exact definition is implementation
     * defined, but usually this would be a full path to the on-disk file for the
     * module.
     */
    @JvmField
    @JsonProperty("path")
    var path: String? = null

    /**
     * True if the module is optimized.
     */
    @JsonProperty("isOptimized")
    var isOptimized: Boolean? = null

    /**
     * True if the module is considered 'user code' by a debugger that supports
     * 'Just My Code'.
     */
    @JsonProperty("isUserCode")
    var isUserCode: Boolean? = null

    /**
     * Version of Module.
     */
    @JvmField
    @JsonProperty("version")
    var version: String? = null

    /**
     * User-understandable description of if symbols were found for the module
     * (ex: 'Symbols Loaded', 'Symbols not found', etc.)
     */
    @JsonProperty("symbolStatus")
    var symbolStatus: String? = null

    /**
     * Logical full path to the symbol file. The exact definition is
     * implementation defined.
     */
    @JvmField
    @JsonProperty("symbolFilePath")
    var symbolFilePath: String? = null

    /**
     * Module created or modified, encoded as a RFC 3339 timestamp.
     */
    @JsonProperty("dateTimeStamp")
    var dateTimeStamp: String? = null

    /**
     * Address range covered by this module.
     */
    @JsonProperty("addressRange")
    var addressRange: String? = null

    override fun toString(): String {
        return "$id${version?.let{" v$it"} ?: ""}${path?.let{" ($it)"} ?: ""}"
    }
}
