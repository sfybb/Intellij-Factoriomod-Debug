package factorio.debugger.thirdparty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.javascript.debugger.JSDebuggerSupportUtils;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.evaluation.ExpressionInfo;
import factorio.debugger.FactorioDebugHoverInfo;

public class JsDebugEval implements FactorioDebugHoverInfo {

    private static FileType typeScriptFileType;
    private static FileType javaScriptFileType;


    public JsDebugEval() {
        Language typeScript = Language.findLanguageByID("TypeScript");
        typeScriptFileType = typeScript != null ? typeScript.getAssociatedFileType() : null;

        Language javaScript = Language.findLanguageByID("JavaScript");
        javaScriptFileType = javaScript != null ? javaScript.getAssociatedFileType() : null;
    }

    @Override
    public boolean supportsFileType(@NotNull final FileType fileType) {
        return fileType.equals(typeScriptFileType) || fileType.equals(javaScriptFileType);
    }

    @Override
    public @NotNull Promise<ExpressionInfo> getExpressionInfoAtOffsetAsync(final @NotNull Project project,
                                                                           final @NotNull Document document,
                                                                           final int offset,
                                                                           final boolean sideEffectsAllowed) {
        return Promises.resolvedPromise(JSDebuggerSupportUtils.getExpressionAtOffset(project, document, offset));
    }
}
