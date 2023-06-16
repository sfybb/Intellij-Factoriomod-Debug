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
    private final @NotNull FactorioSourcePosition sourcePosition;
    private final @NotNull DAPStackFrame myStackFrame;
    private final @NotNull FactorioExecutionStack myExecutionStack;
    private final @NotNull FactorioDebugProcess myDebugProcess;
    private final @NotNull FactorioDebuggerEvaluator myEvaluator;
    private final int myStackFrameId;
    public FactorioStackFrame(@NotNull final FactorioDebugProcess debugProcess,
                              @NotNull final FactorioExecutionStack executionStack,
                              @NotNull final DAPStackFrame stackFrame) {
        this.myStackFrame = stackFrame;
        this.myExecutionStack = executionStack;
        this.myDebugProcess = debugProcess;
        this.myStackFrameId = stackFrame.id;
        this.myEvaluator = new FactorioDebuggerEvaluator(debugProcess, executionStack, myStackFrameId);

        String path = stackFrame.source != null ? stackFrame.source.path : null;
        sourcePosition = myDebugProcess.getSourcePosition(path, myStackFrame.line);
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
        return sourcePosition.getSourcePosition();
    }

    @Override
    public void customizePresentation(@NotNull final ColoredTextContainer component) {
        component.setIcon(AllIcons.Debugger.Frame);
        if (this.sourcePosition.getSourcePosition() != null) {
            if (this.myStackFrame.name != null) {
                component.append(this.myStackFrame.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
                component.append(":" + (this.sourcePosition.getSourceLine() + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                component.append(" " + this.sourcePosition.getPresentablePath(), SimpleTextAttributes.GRAY_ATTRIBUTES);
            } else {
                component.append(this.sourcePosition.getPresentablePath(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                component.append(":" + (this.sourcePosition.getLine() + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                component.setIcon(AllIcons.Debugger.Frame);
            }
        } else if (myStackFrame.source != null ) {
            component.append(myStackFrame.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(":" + (myStackFrame.line), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.append(" " + this.sourcePosition.getPresentablePath(), SimpleTextAttributes.GRAY_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else if (myStackFrame.name != null) {
            component.append(myStackFrame.name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
            component.setIcon(AllIcons.Debugger.Frame);
        } else {
            component.append(ColoredText.singleFragment(XDebuggerBundle.message("message.frame.is.not.available")));
        }

        /*if ( this.myStackFrame.source != null) {
            component.append("  " + this.myStackFrame.source.name + ":" + this.myStackFrame.line, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }*/
    }

    @Override
    public void computeChildren(@NotNull final XCompositeNode node) {
        this.myExecutionStack.getScope(myStackFrame.id).onProcessed(scopesResponse -> {
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
