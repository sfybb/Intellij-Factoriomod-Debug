package factorio.debugger.frames;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredText;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import factorio.debugger.DAP.messages.types.DAPScope;
import factorio.debugger.DAP.messages.types.DAPStackFrame;
import factorio.debugger.FactorioDebugProcess;
import factorio.debugger.FactorioDebuggerEvaluator;

public class FactorioStackFrame extends XStackFrame {
    private XSourcePosition sourcePosition;
    private final DAPStackFrame stackFrame;
    private @NotNull final FactorioExecutionStack myExecutionStack;
    private @NotNull final FactorioDebugProcess myDebugProcess;
    private @NotNull final FactorioDebuggerEvaluator myEvaluator;
    private final int myStackFrameId;
    public FactorioStackFrame(@NotNull final FactorioDebugProcess debugProcess,
                              @NotNull final FactorioExecutionStack executionStack,
                              @NotNull final DAPStackFrame stackFrame) {
        this.stackFrame = stackFrame;
        this.myExecutionStack = executionStack;
        this.myDebugProcess = debugProcess;
        this.myStackFrameId = stackFrame.id;
        this.myEvaluator = new FactorioDebuggerEvaluator(debugProcess, executionStack, myStackFrameId);

        String path = stackFrame.source != null ? stackFrame.source.path : null;
        sourcePosition = myDebugProcess.getSourcePosition(
            new FactorioSourcePosition(
                path,
                stackFrame.line
            ));
    }

    public int getStackFrameId() {
        return myStackFrameId;
    }

    @Override
    public @Nullable XDebuggerEvaluator getEvaluator() {
        return myEvaluator;
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        return sourcePosition;
    }

    @Override
    public void customizePresentation(@NotNull final ColoredTextContainer component) {
        if (this.stackFrame.name != null && this.sourcePosition != null) {
            component.append(this.stackFrame.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(":" + (this.sourcePosition.getLine() + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(", " + this.sourcePosition.getFile().getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(" (" + this.sourcePosition.getFile().getPath() + ")", SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else if (this.sourcePosition != null && this.sourcePosition.getFile().exists()) {
            component.append(this.sourcePosition.getFile().getPath(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(":" + (this.sourcePosition.getLine() + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else if (stackFrame.source != null ) {
            component.append(stackFrame.source.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(":" + (stackFrame.line), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else if (stackFrame.name != null) {
            component.append(stackFrame.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else {
            component.append(ColoredText.singleFragment(XDebuggerBundle.message("message.frame.is.not.available")));
        }
    }

    @Override
    public void computeChildren(@NotNull final XCompositeNode node) {
        this.myExecutionStack.getScope(stackFrame.id).onProcessed(scopesResponse -> {
            DAPScope[] scopes = scopesResponse.body.scopes;

            if (scopes == null || scopes.length == 0) {
                node.addChildren(XValueChildrenList.EMPTY, true);
                return;
            }

            XValueChildrenList childrenList = new XValueChildrenList(scopes.length);

            node.addChildren(childrenList, true);

            boolean isFirstScope = true;
            for (final DAPScope scope : scopes) {
                childrenList.addTopGroup(new FactorioScopeGroup(scope, isFirstScope, this.myExecutionStack));
                isFirstScope = false;
            }
        });
    }
}
