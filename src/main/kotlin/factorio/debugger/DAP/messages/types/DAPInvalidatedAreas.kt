package factorio.debugger.DAP.messages.types;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum DAPInvalidatedAreas {
    @JsonProperty("all") ALL,
    @JsonProperty("stacks") STACKS,
    @JsonProperty("threads") THREADS,
    @JsonProperty("variables") VARIABLES;
}
