package factorio.debugger.frames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import factorio.debugger.DAP.messages.types.DAPVariable;

public class FactorioVariableValueGroup extends FactorioVariableValue implements FactorioVariableContainer {
    public static int MAX_CHILDREN_PER_PAGE = 25;

    private final int myContentRefId;

    private final int myNumChildren;
    private @NotNull final Map<String, Integer> myChildrenMap;
    private List<FactorioVariableValue> myChildrenList;
    private XValueChildrenList myUiChildrenNode;

    public FactorioVariableValueGroup(@NotNull final FactorioExecutionStack executionStack,
                                      @Nullable final FactorioVariableContainer parent,
                                      @NotNull DAPVariable variable) {
        super(executionStack, parent, variable);
        this.myContentRefId = variable.variablesReference;
        this.myUiChildrenNode = null;

        this.myNumChildren = (variable.namedVariables != null ? variable.namedVariables : 0) +
            (variable.indexedVariables != null ? variable.indexedVariables : 0);

        this.myChildrenMap = new HashMap<>();
        this.myChildrenList = null;
    }

    public FactorioVariableValueGroup( @NotNull final FactorioExecutionStack executionStack,
                                       @Nullable final FactorioVariableContainer parent,
                                       @NotNull String name,
                                       @Nullable String value,
                                       int contentRef) {
        super(executionStack, parent, name, value != null ? value : "", null);
        this.myContentRefId = contentRef;
        this.myUiChildrenNode = null;

        this.myNumChildren = -1;

        this.myChildrenMap = new HashMap<>();
        this.myChildrenList = null;
    }

    public @Nullable Icon getIcon() {
        return AllIcons.Debugger.Value;
    }

    public @Nullable @NlsSafe String getComment() {
        return myValue;
    }

    @Override
    public void computeChildren(@NotNull final XCompositeNode node) {
        if (this.myContentRefId == 0) {
            node.addChildren(XValueChildrenList.EMPTY, true);
            return;
        }

        // TODO paging

        if (myUiChildrenNode != null) {
            node.setAlreadySorted(true);

            boolean isComplete = this.myNumChildren == -1 || this.myChildrenList.size() == this.myNumChildren;
            node.addChildren(myUiChildrenNode, isComplete);

            if(!isComplete) {
                node.tooManyChildren(this.myNumChildren - this.myChildrenList.size(), () -> this.addChildren(node, this.myChildrenList.size()));
            }
            return;
        }

        myExecutionStack.getVariableChildren(this, this.myContentRefId, MAX_CHILDREN_PER_PAGE).onProcessed(childList -> {
            this.myChildrenList = childList;

            XValueChildrenList childrenList = new XValueChildrenList(this.myChildrenList.size());
            this.myUiChildrenNode = childrenList;

            int indx = 0;
            for (final FactorioVariableValue child : this.myChildrenList) {
                this.myChildrenMap.put(child.getName(), indx++);
                this.myUiChildrenNode.add(child);
            }

            doAddChildren(node, childrenList);
        });
    }

    protected void doAddChildren(final @NotNull XCompositeNode node, XValueChildrenList childrenList) {
        node.setAlreadySorted(true);

        if (this.myNumChildren == -1 || this.myChildrenList.size() >= this.myNumChildren || this.myChildrenList.size() % MAX_CHILDREN_PER_PAGE != 0) {
            node.addChildren(childrenList, true);
        } else {
            node.addChildren(childrenList, false);
            final int childrenOffset = this.myChildrenList.size();
            node.tooManyChildren(this.myNumChildren - this.myChildrenList.size(), () -> this.addChildren(node, childrenOffset));
        }
    }

    protected void addChildren(@NotNull final XCompositeNode node, final int offset) {
        myExecutionStack.getVariableChildren(
            this,
            this.myContentRefId,
            MAX_CHILDREN_PER_PAGE,
            this.myChildrenList.size())
            .onProcessed(nextChildList -> {
                this.myChildrenList = nextChildList;

                XValueChildrenList nextChildrenList = new XValueChildrenList(this.myChildrenList.size());


                for (int i = offset; i < myChildrenList.size(); i++) {
                    final FactorioVariableValue child = myChildrenList.get(i);

                    this.myChildrenMap.put(child.getName(), i);
                    this.myUiChildrenNode.add(child);

                    nextChildrenList.add(child);
                }

                doAddChildren(node, nextChildrenList);
            });
    }

    @Override
    public void computePresentation(@NotNull final XValueNode node, @NotNull final XValuePlace place) {
        node.setPresentation(getIcon(), new TableValuePresentation(this.getComment(), this.myContentRefId, null), true);
    }

    @Override
    public boolean modify(@NotNull final String variableName, final DAPVariable newValue) {
        Integer varIndex = this.myChildrenMap.get(variableName);
        if (varIndex == null || varIndex < 0 || varIndex >= this.myChildrenList.size()) return false;

        newValue.name = variableName;
        FactorioVariableValue myNewValue = FactorioVariableValue.create(this.myExecutionStack, this, newValue);
        this.myChildrenList.set(varIndex, myNewValue);

        return true;
    }

    @Override
    public boolean hasChild(@NotNull final String variableName) {
        return this.myChildrenMap.containsKey(variableName);
    }

    @Override
    public int getReferenceId() {
        return this.myContentRefId;
    }
}
