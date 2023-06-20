package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPChecksum extends DAPAdditionalProperties {
    /**
     * The algorithm used to calculate this checksum.
     */
    @JsonProperty("algorithm")
    public ChecksumAlgorithm algorithm;
    public enum ChecksumAlgorithm {
        @JsonProperty("MD5")
        MD5,
        @JsonProperty("SHA1")
        SHA1,
        @JsonProperty("SHA256")
        SHA256,
        @JsonProperty("timestamp")
        TIMESTAMP
    }

    /**
     * Value of the checksum, encoded as a hexadecimal value.
     */
    @JsonProperty("checksum")
    public String checksum;
}
