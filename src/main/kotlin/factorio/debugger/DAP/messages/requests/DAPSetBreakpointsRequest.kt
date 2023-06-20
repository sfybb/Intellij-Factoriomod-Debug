package factorio.debugger.DAP.messages.requests

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XSourcePosition
import factorio.debugger.DAP.messages.DAPAdditionalProperties
import factorio.debugger.DAP.messages.requests.DAPSetBreakpointsRequest.SetBreakpointsArguments
import factorio.debugger.DAP.messages.types.DAPSource
import factorio.debugger.DAP.messages.types.DAPSourceBreakpoint

@JsonTypeName("setBreakpoints")
class DAPSetBreakpointsRequest : DAPRequest<SetBreakpointsArguments> {
    constructor() : super(SetBreakpointsArguments())

    constructor(file: VirtualFile, breakpointLines: List<Int>)
            : super(SetBreakpointsArguments(file, breakpointLines))

    constructor(breakpointPos: XSourcePosition)
            : super(SetBreakpointsArguments(breakpointPos.file, breakpointPos.line))

    class SetBreakpointsArguments : DAPAdditionalProperties {
        constructor(file: VirtualFile, breakpointLines: List<Int>) {
            source = DAPSource(file)

            val bpList = ArrayList<DAPSourceBreakpoint>()
            val linesList = ArrayList<Int>()

            for (i in breakpointLines.indices) {
                bpList.add(DAPSourceBreakpoint(breakpointLines[i]))
                linesList.add(breakpointLines[i])
            }

            breakpoints = bpList.toArray(arrayOf<DAPSourceBreakpoint>())
            lines = linesList.toIntArray()
        }

        constructor(file: VirtualFile, breakpointLine: Int) {
            source = DAPSource(file)
            breakpoints = arrayOf(DAPSourceBreakpoint(breakpointLine))
            lines = intArrayOf(breakpointLine)
        }

        constructor()

        /**
         * The source location of the breakpoints; either `source.path` or
         * `source.sourceReference` must be specified.
         */
        @JsonProperty("source")
        lateinit var source: DAPSource

        /**
         * The code locations of the breakpoints.
         */
        @JsonProperty("breakpoints")
        var breakpoints: Array<DAPSourceBreakpoint>? = arrayOf()

        /**
         * Deprecated: The code locations of the breakpoints.
         */
        @JsonProperty("lines")
        var lines: IntArray? = intArrayOf()

        /**
         * A value of true indicates that the underlying source has been modified
         * which results in new breakpoint locations.
         */
        @JsonProperty("sourceModified")
        var sourceModified = false
    }
}
