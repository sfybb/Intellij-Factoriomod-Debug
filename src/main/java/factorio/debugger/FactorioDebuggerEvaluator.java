package factorio.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.ExpressionInfo;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.frames.FactorioExecutionStack;
import factorio.debugger.frames.FactorioVariableValue;

public class FactorioDebuggerEvaluator extends XDebuggerEvaluator {

    private static final ExtensionPointName<FactorioDebugHoverInfo> EP_HOVER_INFO =
        ExtensionPointName.create("factorio.debugger.factorioDebugHoverExpression");
    private @NotNull final FactorioDebugProcess myDebugProcess;
    private @NotNull final FactorioExecutionStack myExecutionStack;
    private final int myFrameId;

    public FactorioDebuggerEvaluator(@NotNull final FactorioDebugProcess debugProcess,
                                     @NotNull final FactorioExecutionStack executionStack,
                                     int frameId) {
        this.myDebugProcess = debugProcess;
        this.myExecutionStack = executionStack;
        this.myFrameId = frameId;
    }

    @Override
    public void evaluate(@NotNull final String expression, @NotNull final XEvaluationCallback callback, @Nullable final XSourcePosition expressionPosition) {
        DAPEvaluateRequest.EvalContext context = null;
        if( this.myDebugProcess.hasCapability(DAPCapabilitiesEnum.EvaluateForHovers) &&
            expressionPosition != null) {
            // This is not 100% correct - the context could be "watch" as well
            context = DAPEvaluateRequest.EvalContext.HOVER;
        }

        myDebugProcess.evaluate(expression, myFrameId, context)
            .onSuccess(evalRes ->
                callback.evaluated(
                    FactorioVariableValue.create(
                        myExecutionStack,
                        evalRes,
                        null
                )))
            .onError(errMsg ->
                    callback.errorOccurred(
                        errMsg != null ? errMsg.getMessage() : "Unknown error occurred")
            );
    }
    @Override
    public @NotNull Promise<ExpressionInfo> getExpressionInfoAtOffsetAsync(@NotNull final Project project,
                                                                           @NotNull final Document document,
                                                                           final int offset,
                                                                           final boolean sideEffectsAllowed) {
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) return Promises.resolvedPromise(null);
        FileType hoverFileType = file.getFileType();


        for (FactorioDebugHoverInfo extension : EP_HOVER_INFO.getExtensionList()) {
            if(extension.supportsFileType(hoverFileType)) {
                return extension.getExpressionInfoAtOffsetAsync(project, document, offset, sideEffectsAllowed);
            }
        }

        return Promises.resolvedPromise(null);
    }
}
