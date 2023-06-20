package factorio.debugger.DAP.messages.types

import com.fasterxml.jackson.annotation.JsonProperty


// TODO add possibility to accept string
// 'all' | 'stacks' | 'threads' | 'variables'
//    | string;
enum class DAPInvalidatedAreas {
    @JsonProperty("all")
    ALL,
    @JsonProperty("stacks")
    STACKS,
    @JsonProperty("threads")
    THREADS,
    @JsonProperty("variables")
    VARIABLES
}
