package factorio.debugger.thirdparty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.ExpressionInfo;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.tang.intellij.lua.debugger.LuaDebuggerEvaluator;
import factorio.debugger.FactorioDebugHoverInfo;

public class LuaDebugEval extends LuaDebuggerEvaluator implements FactorioDebugHoverInfo {
    private final @Nullable FileType luaFileType;

    public LuaDebugEval() {
        Language lua = Language.findLanguageByID("Lua");
        luaFileType = lua != null ? lua.getAssociatedFileType() : null;
    }

    @Override
    protected void eval(@NotNull final String s,
                        @NotNull final XDebuggerEvaluator.XEvaluationCallback xEvaluationCallback,
                        @Nullable final XSourcePosition xSourcePosition) {

    }

    @Override
    public @NotNull Promise<ExpressionInfo> getExpressionInfoAtOffsetAsync(@NotNull final Project project,
                                                                           @NotNull final Document document,
                                                                           final int offset,
                                                                           final boolean sideEffectsAllowed) {
        AsyncPromise<ExpressionInfo> exprInfoProm = new AsyncPromise<>();
        ApplicationManager.getApplication().invokeLater(() -> {
            exprInfoProm.setResult(super.getExpressionInfoAtOffset(project, document, offset, sideEffectsAllowed));
        }, ModalityState.current());

        return exprInfoProm;
    }

    @Override
    public boolean supportsFileType(@NotNull final FileType fileType) {
        return fileType.equals(luaFileType);
    }
}
