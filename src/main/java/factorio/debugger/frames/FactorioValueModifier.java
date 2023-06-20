package factorio.debugger.frames;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.frame.XValueModifier;

public class FactorioValueModifier extends XValueModifier {
    private @NotNull final FactorioExecutionStack myExeutionStack;
    private @Nullable final FactorioVariableContainer myVariableContainer;
    private @NotNull final String myVaribleName;
    private @NotNull final String myInitialValue;
    public FactorioValueModifier(final @NotNull FactorioExecutionStack executionStack,
                                 final @NotNull FactorioVariableValue factorioVariableValue) {
        this.myExeutionStack = executionStack;
        this.myVariableContainer = factorioVariableValue.getMyParent();
        this.myVaribleName = factorioVariableValue.getName();
        this.myInitialValue = factorioVariableValue.getValue();
    }

    @Override
    public void setValue(@NotNull final XExpression expression, @NotNull final XModificationCallback callback) {
        this.myExeutionStack.setValue(this.myVariableContainer, this.myVaribleName, expression.getExpression())
            .onSuccess(dapVar -> {
                boolean success = this.myVariableContainer != null && this.myVariableContainer.modify(this.myVaribleName, dapVar);
                if (success) {
                    callback.valueModified();
                } else {
                    String errorMsg = this.myVariableContainer != null ?
                        String.format("Unable to modify value in container \"%s\"", this.myVariableContainer.getName()) :
                        "Unable to modify value in non existent container";

                    callback.errorOccurred(errorMsg);
                }
            })
            .onError(err -> callback.errorOccurred(err != null ? err.getMessage() : "Unknown error occurred"));
    }

    @Override
    public void calculateInitialValueEditorText(final XInitialValueCallback callback) {
        callback.setValue(this.myInitialValue);
    }
}
