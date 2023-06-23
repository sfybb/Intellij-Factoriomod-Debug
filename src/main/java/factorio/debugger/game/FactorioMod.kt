package factorio.debugger.game

import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XSourcePosition
import factorio.debugger.DAP.messages.types.DAPModule
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.exists

class FactorioMod(private val myId: String, private val myName: String, private val myVersion: String?,
                  private val myRootFile: VirtualFile, projectPath: Path?) {

    private val myScripts: MutableMap<Path, FactorioScript>
    private val mySourceMappedFiles: MutableMap<Path, Path>

    /**
     * Path of this mod zip file / directory in the factorio mod directory
     */
    private val myBasePath: Path


    /**
     * Path of this mod relative to the current project - null if it is not contained in the current project
     */
    private val myProjectPath: Path?

    init {
        mySourceMappedFiles = HashMap()
        myScripts = HashMap()
        myBasePath = Path.of(myRootFile.path)
        myProjectPath = projectPath

        if (myRootFile.isDirectory) {
            // Find Source maps
            searchForSourceMaps()
        }

    }

    private fun searchForSourceMaps() {
        VfsUtilCore.iterateChildrenRecursively(myRootFile,
            { file -> if (file.isDirectory) !DIRECTORIES_TO_IGNORE.contains(file.name) else file.extension.equals("lua")},
            { fileOrDir ->
                if (!fileOrDir.isDirectory && fileOrDir.extension == "lua") {
                    createScript(fileOrDir)
                }
                true
            })
    }
    fun findScript(scriptPath: Path): FactorioScript? {
        val relPath = myBasePath.relativize(scriptPath)
        return myScripts[relPath]
    }

    private fun createScript(scriptPath: Path?): FactorioScript? {
        if (scriptPath == null) return null
        val relPath = myBasePath.relativize(scriptPath)

        if (myScripts.contains(relPath)) return myScripts[relPath]
        val file = myRootFile.findFileByRelativePath(relPath.toString()) ?: return null

        val newScript = FactorioScript(this, file, relPath)
        myScripts[relPath] = newScript

        for (sourceFile in newScript.sourceFiles) {
            mySourceMappedFiles[relativizePath(sourceFile)] = relPath
        }

        return newScript
    }

    private fun createScript(file: VirtualFile?): FactorioScript? {
        if (file == null) return null
        return createScript(Path.of(file.path))
    }

    val presentableId: String
        get() = "__${myId}__"
    val zipPath: Path
        get() = myBasePath

    val id: String
        get() = myId

    fun getRelativeFile(relativeChild: Path): VirtualFile? {
        return myRootFile.findFileByRelativePath(relativeChild.toString())
    }

    private fun relativizePath(filePath: Path): Path {
        val absFilePath: Path = if (!filePath.isAbsolute) {
            val projectRel = myProjectPath?.resolve(filePath)?.normalize()
            if (projectRel?.exists() == true) {
                projectRel
            } else {
                val baseRel = myBasePath.resolve(filePath).normalize()
                if (baseRel.exists()) baseRel else filePath
            }
        } else {
            filePath.normalize()
        }

        return if (absFilePath.startsWith(myBasePath))
            myBasePath.relativize(absFilePath) else
            myProjectPath?.relativize(absFilePath) ?: absFilePath
    }

    fun getFile(filePath: Path): VirtualFile? {
        return getRelativeFile(relativizePath(filePath))
    }

    fun getSourceFile(filePath: Path): VirtualFile? {
        val projectPath = myProjectPath?.resolve(relativizePath(filePath))
        return projectPath?.let { LocalFileSystem.getInstance().findFileByPath(it.toString()) }
    }


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
        val relPath = myProjectPath?.relativize(fPosition.file.toNioPath()) ?: myBasePath.relativize(fPosition.file.toNioPath())

        // TODO factorio path to lua
        val script = myScripts[relPath] ?: return fPosition
        val convertedPosition = script.convertFactorioToSource(fPosition.line, relPath)

        val vFile = getSourceFile(convertedPosition.second)
        return XDebuggerUtil.getInstance().createPosition(vFile, convertedPosition.first) ?: fPosition
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
        // TODO move to correct position in FactorioLocalPositionConverter
        // fmtk starts lines at 1 but jetbrains starts at 0 (fmtk doens't support "linesStartAt1=false" in the initialize request)
        val relSPath = relativizePath(Path.of(sPosition.file.path))

        val script = myScripts[mySourceMappedFiles[relSPath]] ?: myScripts[relSPath] ?: return sPosition
        val convertedPosition = script.convertSourceToFactorio(sPosition.line, relSPath)

        val vFile = getFile(convertedPosition.second)
        return XDebuggerUtil.getInstance().createPosition(vFile, convertedPosition.first) ?: sPosition
    }

    fun containsFile(sFile: Path): Boolean {
        val absFilePath = sFile.toAbsolutePath()
        return absFilePath.startsWith(myBasePath) || myProjectPath?.let { absFilePath.startsWith(it) } ?: false
    }

    override fun toString(): String {
        return if (myVersion != null) "$myName v$myVersion" else myName
    }

    fun getPresentablePath(file: VirtualFile?): String? {
        if (file != null) {
            val relPath = relativizePath(Path.of(file.path))
            return if (file.isInLocalFileSystem) relPath.toString() else "__${myId}__/${relPath}"
        }
        return null
    }

    companion object {
        public val DIRECTORIES_TO_IGNORE = setOf("node_modules", ".git", ".idea", ".vscode")
        fun fromModule(module: DAPModule, projectPath: Path?, factorioEnv: FactorioGameRuntimeEnvironment): FactorioMod? {
            if (module.id == "#user") return null

            val pathString = module.path ?: module.symbolFilePath

            val path: Path
            val modRootFile: VirtualFile?
            if (pathString != null) {
                path = Path.of(URI.create(pathString))
                modRootFile = LocalFileSystem.getInstance().findFileByPath(path.toString())
            }  else {
                path = Path.of(factorioEnv.modFolder, module.id + "_" + module.version + ".zip")
                val modZipFile = LocalFileSystem.getInstance().findFileByPath(path.toString())

                modRootFile = if (modZipFile != null) JarFileSystem.getInstance().getJarRootForLocalFile(modZipFile) else null
            }

            return if (modRootFile != null) {
                FactorioMod(module.id, module.name, module.version, modRootFile, projectPath)
            } else null
        }
    }
}
