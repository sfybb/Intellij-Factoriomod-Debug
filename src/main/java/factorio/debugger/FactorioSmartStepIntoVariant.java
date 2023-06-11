package factorio.debugger;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.stepping.XSmartStepIntoVariant;
import factorio.debugger.DAP.messages.types.DAPStepInTarget;

public class FactorioSmartStepIntoVariant extends XSmartStepIntoVariant {
    private final DAPStepInTarget myTarget;
    private @Nullable final TextRange myTextRange;

    public FactorioSmartStepIntoVariant(@NotNull final DAPStepInTarget target, VirtualFile file) {
        this.myTarget = target;
        this.myTarget.label = this.myTarget.label != null ? this.myTarget.label : "";

        Document document = FileDocumentManager.getInstance().getDocument(file);
        if(document != null && myTarget.line != null && myTarget.endLine != null) {
            int startOffset = document.getLineStartOffset(myTarget.line);
            if(myTarget.column != null) {
                startOffset += myTarget.column;
            }

            int endOffset = myTarget.endColumn != null ? myTarget.endColumn : 0;
            if (endOffset != 0) {
                endOffset += document.getLineStartOffset(myTarget.endLine);
            } else {
                endOffset = document.getLineEndOffset(myTarget.endLine);
            }

            myTextRange = TextRange.create(startOffset, endOffset);
        } else {
            myTextRange = null;
        }
    }

    public Integer getTargetId() {
        return myTarget.id;
    }

    @Override
    public @NlsSafe String getText() {
        return myTarget.label;
    }

    @Override
    public @Nullable TextRange getHighlightRange() {
        return myTextRange;
    }

    @Override
    public @Nullable @Nls String getDescription() {
        return myTarget.label;
    }
}
