package factorio.debugger.frames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueGroup;
import factorio.debugger.DAP.messages.types.DAPScope;
import factorio.debugger.DAP.messages.types.DAPVariable;

public class FactorioScopeGroup extends XValueGroup implements FactorioVariableContainer {
    private final String scopeType;
    private final boolean firstScope;
    private final DAPScope myScope;
    private final FactorioExecutionStack myExecutionStack;
    private final int myContentRefId;
    private @NotNull final Map<String, Integer> myChildrenMap;
    private List<FactorioVariableValue> myChildrenList;
    private XValueChildrenList myUiChildrenNode;

    protected FactorioScopeGroup(@NotNull final DAPScope scope, boolean firstScope,
                                 @NotNull final FactorioExecutionStack executionStack) {
        super(scope.name);
        this.myExecutionStack = executionStack;
        this.myContentRefId = scope.variablesReference;
        this.myScope = scope;
        this.scopeType = scope.presentationHint;
        this.firstScope = firstScope;
        this.myChildrenMap = new HashMap<>();
        this.myChildrenList = null;
    }

    @Override
    public boolean isAutoExpand() {
        return "local".equals(scopeType) || "arguments".equals(scopeType) || firstScope ;
    }

    @Override
    public void computeChildren(@NotNull final XCompositeNode node) {
        if (this.myContentRefId == 0) {
            node.addChildren(XValueChildrenList.EMPTY, true);
            return;
        }

        if (myUiChildrenNode != null) {
            node.setAlreadySorted(true);
            node.addChildren(myUiChildrenNode, true);
            return;
        }

        // TODO add pagign - anythong more than ~100 results gets cut off
        myExecutionStack.getVariableChildren(this, this.myContentRefId, XCompositeNode.MAX_CHILDREN_TO_SHOW).onProcessed(childList -> {
            this.myChildrenList = childList;

            XValueChildrenList childrenList = new XValueChildrenList(this.myChildrenList.size());
            this.myUiChildrenNode = childrenList;

            int indx = 0;
            for (final FactorioVariableValue child : this.myChildrenList) {
                this.myChildrenMap.put(child.getName(), indx++);
                this.myUiChildrenNode.add(child);
            }


            node.setAlreadySorted(true);
            node.addChildren(childrenList, true);
        });
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
    public @Nullable FactorioVariableContainer getParent() {
        return null;
    }

    @Override
    public int getReferenceId() {
        return this.myContentRefId;
    }
}
