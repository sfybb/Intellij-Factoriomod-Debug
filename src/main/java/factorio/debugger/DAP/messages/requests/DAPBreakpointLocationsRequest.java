package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;
import factorio.debugger.DAP.messages.types.DAPSource;

@JsonTypeName("breakpointLocations")
public class DAPBreakpointLocationsRequest extends DAPRequest<DAPBreakpointLocationsRequest.BreakpointLocationsArguments> {
    public DAPBreakpointLocationsRequest() {
        this.arguments = new BreakpointLocationsArguments();
    }

    public static class BreakpointLocationsArguments extends DAPAdditionalProperties {
        public BreakpointLocationsArguments() {
            source = new DAPSource();
        }
        /**
         * The source location of the breakpoints; either `source.path` or
         * `source.reference` must be specified.
         */
        @JsonProperty("source")
        public DAPSource source;

        /**
         * Start line of range to search possible breakpoint locations in. If only the
         * line is specified, the request returns all possible locations in that line.
         */
        @JsonProperty("line")
        public int line;

        /**
         * Start position within `line` to search possible breakpoint locations in. It
         * is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based. If no column is
         * given, the first position in the start line is assumed.
         */
        @JsonProperty("column")
        public Integer column;

        /**
         * End line of range to search possible breakpoint locations in. If no end
         * line is given, then the end line is assumed to be the start line.
         */
        @JsonProperty("endLine")
        public Integer endLine;

        /**
         * End position within `endLine` to search possible breakpoint locations in.
         * It is measured in UTF-16 code units and the client capability
         * `columnsStartAt1` determines whether it is 0- or 1-based. If no end column
         * is given, the last position in the end line is assumed.
         */
        @JsonProperty("endColumn")
        public Integer endColumn;
    }
}
