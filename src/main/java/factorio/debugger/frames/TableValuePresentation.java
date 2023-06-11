package factorio.debugger.frames;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;

public class TableValuePresentation extends XRegularValuePresentation {
    protected String myComment;
    protected int refId;
    public TableValuePresentation(@Nullable final String comment, int refId,  @Nullable final String type) {
        super(comment != null ? comment : "", type);
        this.myComment = comment;
        this.refId = refId;
    }

    @Override
    public void renderValue(@NotNull final XValueTextRenderer renderer) {
        if(this.myComment != null) renderer.renderComment(this.myComment);
        renderer.renderValue(String.format("Ref: %d", refId));
    }
}
