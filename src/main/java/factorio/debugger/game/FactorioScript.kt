package factorio.debugger.game

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Url
import com.intellij.util.Urls
import com.intellij.util.Urls.newLocalFileUrl
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

class FactorioScript(val myMod: FactorioMod, private val myScriptFile: VirtualFile, private val myRelativePath: Path) {
    private val mySourceMap: SourceMap?
    init {
        val sourceMapPath = myRelativePath.resolveSibling(myRelativePath.fileName.toString() + ".map")
        val sourceMapFile = myMod.getRelativeFile(sourceMapPath)

        mySourceMap = if (sourceMapFile?.exists() == true) {
            try {
                val fileContent = IOUtils.toString(sourceMapFile.inputStream, StandardCharsets.UTF_8)
                decodeSourceMapSafely(fileContent, true,
                    newLocalFileUrl(sourceMapFile.path), true)
            } catch (e: IOException) {
                null
            }
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
     * Converts the line number [fLine] and path [fPath] from the Factorio lua script to the associated source file line.
     * If this script has no source map the position is unchanged otherwise the source mapped line is returned
     * This is the inverse of [convertSourceToFactorio]
     *
     * @param fLine the line in the source file
     * @param fPath the path to the lua script relative to the mod root
     * @return the position in the associated source file
     */
    fun convertFactorioToSource(fLine: Int, fPath: Path): Pair<Int, Path> {
        // TODO move to correct position in FactorioLocalPositionConverter
        // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)

        val entry = mySourceMap?.generatedMappings?.get(fLine, 1)

        if (entry != null && mySourceMap != null) {
            val fileUrl = mySourceMap.sources[entry.source]
            if (fileUrl.isInLocalFileSystem) {
                return Pair(entry.sourceLine, Path.of(fileUrl.path))
            }
        }
        return fLine to fPath
    }

    /**
     * Converts the line number [sLine] and path [sPath] from the source file to the associated Factorio lua script line
     * If this script has no source map the position is unchanged otherwise the reverse source mapped line is returned
     * This is the inverse of [convertFactorioToSource]
     *
     * @param sLine the line in the source file
     * @param sPath the path to the source file relative to the mod root
     * @return the line in the associated source file
     */
    fun convertSourceToFactorio(sLine: Int, sPath: Path): Pair<Int, Path> {
        val fileUrl: Url? = Urls.parseEncoded(sPath.toString())
        val urlIndx = fileUrl?.let { mySourceMap?.sourceResolver?.getSourceIndex(it) } ?: -1

        if (urlIndx >= 0 && mySourceMap != null) {
            val mappings  = mySourceMap.findSourceMappings(urlIndx)

            val entry: MappingEntry? = if (mappings is MappingList) {
                mappings.getMappingsInLine(sLine).first()
            } else {
                mappings.get(sLine, 1)
            }

            if (entry != null) {
                return entry.generatedLine to myRelativePath
            }
        }
        return sLine to sPath
    }
}
