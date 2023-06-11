package factorio.debugger.DAP.messages.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.intellij.openapi.vfs.VirtualFile;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;

public class DAPSource extends DAPAdditionalProperties {
    /**
     * The short name of the source. Every source returned from the debug adapter
     * has a name.
     * When sending a source to the debug adapter this name is optional.
     */
    @JsonProperty("name")
    public String name;

    /**
     * The path of the source to be shown in the UI.
     * It is only used to locate and load the content of the source if no
     * `sourceReference` is specified (or its value is 0).
     */
    @JsonProperty("path")
    public String path;

    /**
     * If the value > 0 the contents of the source must be retrieved through the
     * `source` request (even if a path is specified).
     * Since a `sourceReference` is only valid for a session, it can not be used
     * to persist a source.
     * The value should be less than or equal to 2147483647 (2^31-1).
     */
    @JsonProperty("sourceReference")
    @Nullable public Integer sourceReference;

    /**
     * A hint for how to present the source in the UI.
     * A value of `deemphasize` can be used to indicate that the source is not
     * available or that it is skipped on stepping.
     * Values: 'normal', 'emphasize', 'deemphasize'
     */
    @JsonProperty("presentationHint")
    public PresentationHint presentationHint;

    public DAPSource(@NotNull final VirtualFile file) {
        this.path = file.getPath();
        this.name = file.getName();
    }

    public DAPSource() {

    }

    public enum PresentationHint {
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
    public String origin;

    /**
     * A list of sources that are related to this source. These may be the source
     * that generated this source.
     */
    @JsonProperty("sources")
    public DAPSource[] sources;

    /**
     * Additional data that a debug adapter might want to loop through the client.
     * The client should leave the data intact and persist it across sessions. The
     * client should not interpret the data.
     */
    @JsonProperty("adapterData")
    @JsonRawValue
    public String adapterData;

    /**
     * The checksums associated with this file.
     */
    @JsonProperty("checksums")
    public DAPChecksum[] checksums;
}
