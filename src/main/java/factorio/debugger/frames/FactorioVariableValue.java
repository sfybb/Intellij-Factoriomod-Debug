package factorio.debugger.frames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.evaluation.XInstanceEvaluator;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XErrorValuePresentation;
import com.intellij.xdebugger.frame.presentation.XKeywordValuePresentation;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import factorio.debugger.DAP.messages.response.DAPEvaluateResponse;
import factorio.debugger.DAP.messages.response.DAPVariablesResponse;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.DAP.messages.types.DAPVariable;

public class FactorioVariableValue extends XNamedValue {
    private Logger logger = Logger.getInstance(FactorioVariableValue.class);
    private static Logger slogger = Logger.getInstance(FactorioVariableValue.class);

    protected @Nullable final FactorioVariableContainer myParent;
    protected @NotNull final FactorioExecutionStack myExecutionStack;
    protected @Nullable DAPVariable myVariable;
    protected @Nullable String myType;
    protected @NotNull String myValue;

    public FactorioVariableValue(@NotNull final FactorioExecutionStack executionStack,
                                 @Nullable final FactorioVariableContainer parent,
                                 @NotNull DAPVariable variable) {
        super(trimQuotes(variable.name));
        this.myExecutionStack = executionStack;
        this.myVariable = variable;
        this.myValue = variable.value;
        this.myType = variable.type;
        this.myParent = parent;
    }

    public FactorioVariableValue(@NotNull final FactorioExecutionStack executionStack,
                                 @Nullable final FactorioVariableContainer parent,
                                 @NotNull DAPEvaluateResponse.Body evalResult) {
        super("");
        this.myExecutionStack = executionStack;
        this.myValue = evalResult.result;
        this.myType = evalResult.type;
        this.myVariable = null;
        this.myParent = parent;
    }

    public FactorioVariableValue(@NotNull final FactorioExecutionStack executionStack,
                                 @Nullable final FactorioVariableContainer parent,
                                 @NotNull String name,
                                 @NotNull String value,
                                 @Nullable String type) {
        super(trimQuotes(name));
        this.myExecutionStack = executionStack;
        this.myParent = parent;
        this.myVariable = null;
        this.myValue = value;
        this.myType = type;
    }

    @Override
    public @NotNull Promise<XExpression> calculateEvaluationExpression() {
        if(myVariable == null) return Promises.resolvedPromise(null);

        Language lang = Language.findLanguageByID("Lua");
        String expr = myVariable.evaluateName;

        XExpression res = expr != null ?
            XDebuggerUtil.getInstance().createExpression(expr, lang, null, EvaluationMode.EXPRESSION) :
            null;
        return Promises.resolvedPromise(res);
    }

    @Override
    public @Nullable XInstanceEvaluator getInstanceEvaluator() {
        if(myVariable == null) return null;

        String expr = myVariable.evaluateName;
        if(expr == null) return null;

        return (callback, frame) -> {
            XDebuggerEvaluator evaluator = frame.getEvaluator();
            if(evaluator != null) {
                evaluator.evaluate(expr, callback, null);
            } else {
                callback.evaluated(FactorioVariableValue.this);
            }
        };
    }

    @Override
    public @Nullable XValueModifier getModifier() {
        if (!this.myExecutionStack.hasCapability(DAPCapabilitiesEnum.SetExpression) &&
            !this.myExecutionStack.hasCapability(DAPCapabilitiesEnum.SetVariable)) {
            return null;
        }

        return new FactorioValueModifier(this.myExecutionStack, this);
    }

    @Override
    public void computePresentation(@NotNull final XValueNode node, @NotNull final XValuePlace place) {
        if (myType != null) {
            Icon icon = AllIcons.Debugger.Db_primitive;
            XValuePresentation presentation;

            switch (myType) {
                case "method", "function" -> {
                    icon = AllIcons.Nodes.Lambda;
                    presentation = new XStringValuePresentation(myValue);
                }
                case "number" -> presentation = new XNumericValuePresentation(myValue);
                case "empty", "boolean", "nil" -> presentation = new XKeywordValuePresentation(myValue);
                case "string" -> presentation = new XStringValuePresentation(trimQuotes(myValue));
                default -> presentation = new XStringValuePresentation(myValue + String.format(" (Type: '%s')", myType));
            }

            node.setPresentation(icon, presentation, false);
        } else {
            node.setPresentation(AllIcons.Debugger.Db_primitive, new XErrorValuePresentation(myValue), false);
        }
    }

    private static String trimQuotes(String str) {
        return trimQuotes(str, true);
    }

    private static String trimQuotes(String str, boolean alwaysTrim) {
        if(!alwaysTrim && TextUtils.containsBlanks(str)) return str;

        int startIndx = str.startsWith("\"") ? 1 : 0;
        int endIndx = str.length() + (str.endsWith("\"") ? -1 : 0);
        return str.substring(startIndx, endIndx);
    }

    public static @NotNull XValue create(@NotNull final FactorioExecutionStack executionStack,
                                         @Nullable final DAPEvaluateResponse evalRes,
                                         @Nullable final String exceptionMessage) {
        DAPEvaluateResponse.Body body = evalRes != null ? evalRes.body : null;
        String errorMessage = evalRes != null && evalRes.message != null ? evalRes.message :
            exceptionMessage != null ? exceptionMessage : "An unknown error occurred";
        if(body == null) {
            return new FactorioErrorVariable("result", errorMessage, null);
        }
        if(!evalRes.success) {
            return new FactorioErrorVariable("result", errorMessage, body.type);
        }

        if(body.variablesReference == 0) return new FactorioVariableValue(executionStack, null, body);
        else return new FactorioVariableValueGroup(
            executionStack, null, "result" , body.result,
            body.variablesReference);
    }

    public static @Nullable FactorioVariableValue create(@NotNull final FactorioExecutionStack executionStack,
                                         @Nullable final FactorioVariableContainer parent,
                                         @Nullable final DAPVariable variable) {
        if(variable == null) {
            return null;
        }

        if(variable.variablesReference == 0) return new FactorioVariableValue(executionStack, parent, variable);
        else return new FactorioVariableValueGroup(executionStack, parent, variable);
    }

    public static @NotNull List<FactorioVariableValue> createChildren(
                                                    @NotNull final FactorioExecutionStack executionStack,
                                                    @Nullable final FactorioVariableContainer parent,
                                                    final DAPVariablesResponse varResp) {
        DAPVariable[] variables = varResp != null && varResp.body != null ? varResp.body.variables : null;
        if(variables == null) return Collections.emptyList();

        List<FactorioVariableValue> result =  new ArrayList<>(variables.length);

        for (final DAPVariable variable : variables) {
            if(variable.variablesReference == 0) result.add(new FactorioVariableValue(executionStack, parent, variable));
            else result.add(new FactorioVariableValueGroup(executionStack, parent, variable));
        }

        return result;
    }

    public @Nullable FactorioVariableContainer getParent() {
        return this.myParent;
    }

    public String getValue() {
        return this.myValue;
    }
}
