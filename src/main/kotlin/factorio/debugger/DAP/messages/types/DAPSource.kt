package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRawValue
import com.intellij.openapi.vfs.VirtualFile
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPSource : DAPAdditionalProperties {
    /**
     * The short name of the source. Every source returned from the debug adapter
     * has a name.
     * When sending a source to the debug adapter this name is optional.
     */
    @JsonProperty("name")
    var name: String? = null

    /**
     * The path of the source to be shown in the UI.
     * It is only used to locate and load the content of the source if no
     * `sourceReference` is specified (or its value is 0).
     */
    @JvmField
    @JsonProperty("path")
    var path: String? = null

    /**
     * If the value > 0 the contents of the source must be retrieved through the
     * `source` request (even if a path is specified).
     * Since a `sourceReference` is only valid for a session, it can not be used
     * to persist a source.
     * The value should be less than or equal to 2147483647 (2^31-1).
     */
    @JsonProperty("sourceReference")
    var sourceReference: Int? = null

    /**
     * A hint for how to present the source in the UI.
     * A value of `deemphasize` can be used to indicate that the source is not
     * available or that it is skipped on stepping.
     * Values: 'normal', 'emphasize', 'deemphasize'
     */
    @JsonProperty("presentationHint")
    var presentationHint: PresentationHint? = null

    constructor(file: VirtualFile) {
        path = file.path
        name = file.name
    }

    constructor()

    enum class PresentationHint {
        @JsonProperty("normal")
        NORMAL,
        @JsonProperty("emphasize")
        EMPHASIZE,
        @JsonProperty("deemphasize")
        DEEMPHASIZE
    }

    /**
     * The origin of this source. For example, 'internal module', 'inlined content
     * from source map', etc.
     */
    @JsonProperty("origin")
    var origin: String? = null

    /**
     * A list of sources that are related to this source. These may be the source
     * that generated this source.
     */
    @JsonProperty("sources")
    var sources: Array<DAPSource> = arrayOf()

    /**
     * Additional data that a debug adapter might want to loop through the client.
     * The client should leave the data intact and persist it across sessions. The
     * client should not interpret the data.
     */
    @JsonProperty("adapterData")
    @JsonRawValue
    var adapterData: String? = null

    /**
     * The checksums associated with this file.
     */
    @JsonProperty("checksums")
    var checksums: Array<DAPChecksum> = arrayOf()
}
