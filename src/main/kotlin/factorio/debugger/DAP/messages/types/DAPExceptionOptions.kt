package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty
import factorio.debugger.DAP.messages.DAPAdditionalProperties

class DAPExceptionOptions : DAPAdditionalProperties() {
    /**
     * A path that selects a single or multiple exceptions in a tree. If `path` is
     * missing, the whole tree is selected.
     * By convention the first segment of the path is a category that is used to
     * group exceptions in the UI.
     */
    @JsonProperty("path")
    var path: Array<DAPExceptionPathSegment> = arrayOf()

    /**
     * Condition when a thrown exception should result in a break.
     */
    @JsonProperty("breakMode")
    lateinit var breakMode: DAPExceptionBreakMode
}
