package factorio.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.evaluation.ExpressionInfo;

public interface FactorioDebugHoverInfo {

    boolean supportsFileType(@NotNull FileType fileType);

    @NotNull
    Promise<ExpressionInfo> getExpressionInfoAtOffsetAsync(@NotNull final Project project,
                                                           @NotNull final Document document,
                                                           final int offset,
                                                           final boolean sideEffectsAllowed);
}
