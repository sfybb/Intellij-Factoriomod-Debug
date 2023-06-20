package factorio.debugger.game

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Url
import com.intellij.util.Urls
import com.intellij.util.Urls.newLocalFileUrl
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.jetbrains.debugger.sourcemap.MappingEntry
import org.jetbrains.debugger.sourcemap.MappingList
import org.jetbrains.debugger.sourcemap.SourceMap
import org.jetbrains.debugger.sourcemap.decodeSourceMapSafely
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.security.MessageDigest

class FactorioScript(val mod: FactorioMod, private val myScriptFile: VirtualFile, relativePath: Path) {
    private val mySourceMap: SourceMap?
    init {
        val sourceMapPath = relativePath.resolveSibling(relativePath.fileName.toString() + ".map")
        val sourceMapFile = mod.getRelativeFile(sourceMapPath)

        mySourceMap = if (sourceMapFile?.exists() == true) {
            val fileContent = IOUtils.toString(sourceMapFile.inputStream, StandardCharsets.UTF_8)
            decodeSourceMapSafely(fileContent, true,
                newLocalFileUrl(sourceMapFile.path), true)
        } else {
            null
        }
    }

    fun getChecksum(digest: MessageDigest?): String {
        return try {
            Hex.encodeHexString(DigestUtils.digest(digest, myScriptFile.inputStream))
        } catch (e: IOException) {
            ""
        }
    }

    val isInLocalFileSystem: Boolean
        get() = myScriptFile.isInLocalFileSystem

    val scriptFile: VirtualFile
        get() = myScriptFile

    val sourceFiles: List<Path>
        get() = mySourceMap?.sources?.map { Path.of(it.path) } ?: listOf(Path.of(myScriptFile.path).toAbsolutePath())

    /**
     * Converts the line number [fPosition] from the Factorio lua script to the associated source file line.
     * If this script has no source map the position is unchanged otherwise the source mapped line is returned
     * This is the inverse of [convertSourceToFactorio]
     *
     * @param fPosition the position in the factorio lua script
     * @return the position in the associated source file
     */
    fun convertFactorioToSource(fPosition: XSourcePosition): XSourcePosition {
        // TODO move to correct position in FactorioLocalPositionConverter
        // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
        if (mySourceMap == null) return fPosition

        val entry = mySourceMap.generatedMappings.get(fPosition.line, 1)

        if (entry != null) {
            val fileUrl = mySourceMap.sources[entry.source]
            if (fileUrl.isInLocalFileSystem) {
                val vFile = mod.getSourceFile(Path.of(fileUrl.path))
                return XDebuggerUtil.getInstance().createPosition(vFile, entry.sourceLine) ?: fPosition
            }
        }
        return fPosition
    }

    /**
     * Converts the line number [sPosition] from the source file to the associated Factorio lua script line
     * If this script has no source map the position is unchanged otherwise the reverse source mapped line is returned
     * This is the inverse of [convertFactorioToSource]
     *
     * @param sPosition the position in the source file
     * @return the line in the associated source file
     */
    fun convertSourceToFactorio(sPosition: XSourcePosition): XSourcePosition? {
        if (mySourceMap == null) return sPosition

        val fileUrl: Url? = Urls.parseEncoded(sPosition.file.url)
        val urlIndx = if (fileUrl != null) mySourceMap.sourceResolver.getSourceIndex(fileUrl) else -1

        if (urlIndx >= 0) {
            val mappings  = mySourceMap.findSourceMappings(urlIndx)

            val entry: MappingEntry? = if (mappings is MappingList) {
                mappings.getMappingsInLine(sPosition.line).first()
            } else {
                mappings.get(sPosition.line, 1)
            }

            return if (entry != null) XDebuggerUtil.getInstance().createPosition(myScriptFile, entry.generatedLine) else sPosition
        }
        return sPosition
    }
}
