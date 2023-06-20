package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.intellij.openapi.vfs.VirtualFile
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPBreakpointLocationsRequest.BreakpointLocationsArguments
import factorio.debugger.DAP.messages.types.DAPSource

@JsonTypeName("breakpointLocations")
class DAPBreakpointLocationsRequest(file: VirtualFile, startLine: Int, endLine: Int) :
    DAPRequest<BreakpointLocationsArguments>(BreakpointLocationsArguments(file, startLine, endLine)) {

    class BreakpointLocationsArguments(file: VirtualFile, startLine: Int, endLine: Int) : DAPAdditionalProperties() {
        /**
         * The source location of the breakpoints; either `source.path` or
         * `source.reference` must be specified.
         */
        @JsonProperty("source")
        var source: DAPSource

        /**
         * Start line of range to search possible breakpoint locations in. If only the
         * line is specified, the request returns all possible locations in that line.
         */
        @JsonProperty("line")
        var line: Int

        /**
         * Start position within `line` to search possible breakpoint locations in. It
         * is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based. If no column is
         * given, the first position in the start line is assumed.
         */
        @JsonProperty("column")
        var column: Int? = null

        /**
         * End line of range to search possible breakpoint locations in. If no end
         * line is given, then the end line is assumed to be the start line.
         */
        @JsonProperty("endLine")
        var endLine: Int?

        /**
         * End position within `endLine` to search possible breakpoint locations in.
         * It is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based. If no end column
         * is given, the last position in the end line is assumed.
         */
        @JsonProperty("endColumn")
        var endColumn: Int? = null

        init {
            source = DAPSource(file)
            line = startLine
            this.endLine = endLine
        }
    }
}
