package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPModule extends DAPAdditionalProperties {
    /**
     * Unique identifier for the module.
     */
    @JsonProperty("id")
    @JsonRawValue
    public String id;

    /**
     * A name of the module.
     */
    @JsonProperty("name")
    public String name;

    /**
     * Logical full path to the module. The exact definition is implementation
     * defined, but usually this would be a full path to the on-disk file for the
     * module.
     */
    @JsonProperty("path")
    public String path;

    /**
     * True if the module is optimized.
     */
    @JsonProperty("isOptimized")
    public Boolean isOptimized;

    /**
     * True if the module is considered 'user code' by a debugger that supports
     * 'Just My Code'.
     */
    @JsonProperty("isUserCode")
    public Boolean isUserCode;

    /**
     * Version of Module.
     */
    @JsonProperty("version")
    public String version;

    /**
     * User-understandable description of if symbols were found for the module
     * (ex: 'Symbols Loaded', 'Symbols not found', etc.)
     */
    @JsonProperty("symbolStatus")
    public String symbolStatus;

    /**
     * Logical full path to the symbol file. The exact definition is
     * implementation defined.
     */
    @JsonProperty("symbolFilePath")
    public String symbolFilePath;

    /**
     * Module created or modified, encoded as a RFC 3339 timestamp.
     */
    @JsonProperty("dateTimeStamp")
    public String dateTimeStamp;

    /**
     * Address range covered by this module.
     */
    @JsonProperty("addressRange")
    public String addressRange;
}
