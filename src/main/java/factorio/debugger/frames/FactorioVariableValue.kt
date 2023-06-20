package factorio.debugger.frames

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.XExpression
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator.XEvaluationCallback
import com.intellij.xdebugger.evaluation.XInstanceEvaluator
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.frame.presentation.*
import factorio.debugger.DAP.messages.responses.DAPEvaluateResponse
import factorio.debugger.DAP.messages.responses.DAPVariablesResponse
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum
import factorio.debugger.DAP.messages.types.DAPVariable
import org.apache.http.util.TextUtils
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise

open class FactorioVariableValue : XNamedValue {
    private val logger = Logger.getInstance(FactorioVariableValue::class.java)
    protected val myParent: FactorioVariableContainer?
    @JvmField
    protected val myExecutionStack: FactorioExecutionStack
    protected var myVariable: DAPVariable?
    protected var myType: String?
    var value: String
        protected set

    constructor(
        executionStack: FactorioExecutionStack,
        parent: FactorioVariableContainer?,
        variable: DAPVariable
    ) : super(trimQuotes(variable.name)) {
        myExecutionStack = executionStack
        myVariable = variable
        value = variable.value
        myType = variable.type
        this.myParent = parent
    }

    constructor(
        executionStack: FactorioExecutionStack,
        parent: FactorioVariableContainer?,
        evalResult: DAPEvaluateResponse.EvaluateResponseBody
    ) : super("") {
        myExecutionStack = executionStack
        value = evalResult.result
        myType = evalResult.type
        myVariable = null
        this.myParent = parent
    }

    constructor(
        executionStack: FactorioExecutionStack,
        parent: FactorioVariableContainer?,
        name: String,
        value: String,
        type: String?
    ) : super(trimQuotes(name)) {
        myExecutionStack = executionStack
        this.myParent = parent
        myVariable = null
        this.value = value
        myType = type
    }

    override fun calculateEvaluationExpression(): Promise<XExpression?> {
        val lang = Language.findLanguageByID("Lua")
        val expr = myVariable?.evaluateName
        return resolvedPromise(expr?.let { XDebuggerUtil.getInstance().createExpression(it, lang, null, EvaluationMode.EXPRESSION) })
    }

    override fun getInstanceEvaluator(): XInstanceEvaluator? {
        val expr = myVariable?.evaluateName ?: return null
        return XInstanceEvaluator { callback: XEvaluationCallback, frame: XStackFrame ->
            val evaluator = frame.evaluator
            if (evaluator != null) {
                evaluator.evaluate(expr, callback, null)
            } else {
                callback.evaluated(this@FactorioVariableValue)
            }
        }
    }

    override fun getModifier(): XValueModifier? {
        return if (myExecutionStack.hasCapability(DAPCapabilitiesEnum.SetExpression) ||
                   myExecutionStack.hasCapability(DAPCapabilitiesEnum.SetVariable))
            FactorioValueModifier(myExecutionStack, this)
        else null
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        var icon = AllIcons.Debugger.Db_primitive
        val presentation: XValuePresentation
        when (myType) {
            "method", "function" -> {
                icon = AllIcons.Nodes.Lambda
                presentation = XStringValuePresentation(value)
            }

            "number" -> presentation = XNumericValuePresentation(value)
            "empty", "boolean", "nil" -> presentation = XKeywordValuePresentation(value)
            "string" -> presentation = XStringValuePresentation(trimQuotes(value))
            null -> presentation =  XErrorValuePresentation(value)
            else -> presentation = XStringValuePresentation("$value (Type: '$myType')" )
        }
        node.setPresentation(icon, presentation, false)
    }

    companion object {
        private val slogger = Logger.getInstance(
            FactorioVariableValue::class.java
        )

        private fun trimQuotes(str: String, alwaysTrim: Boolean = true): String {
            if (!alwaysTrim && TextUtils.containsBlanks(str)) return str
            val startIndx = if (str.startsWith("\"")) 1 else 0
            val endIndx = str.length + if (str.endsWith("\"")) -1 else 0
            return str.substring(startIndx, endIndx)
        }

        @JvmStatic
        fun create(
            executionStack: FactorioExecutionStack,
            evalRes: DAPEvaluateResponse?,
            exceptionMessage: String?
        ): XValue {
            val body = evalRes?.body
            val errorMessage = evalRes?.message ?: exceptionMessage ?: "An unknown error occurred"
            if (body == null || !evalRes.success) {
                return FactorioErrorVariable("result", errorMessage, body?.type)
            }
            return if (body.variablesReference == 0) FactorioVariableValue(executionStack, null, body)
                else FactorioVariableValueGroup(
                    executionStack, null, "result", body.result,
                    body.variablesReference
            )
        }

        @JvmStatic
        fun create(
            executionStack: FactorioExecutionStack,
            parent: FactorioVariableContainer?,
            variable: DAPVariable?
        ): FactorioVariableValue? {
            if (variable == null) {
                return null
            }
            return if (variable.variablesReference == 0) FactorioVariableValue(
                executionStack,
                parent,
                variable
            ) else FactorioVariableValueGroup(
                executionStack,
                parent,
                variable
            )
        }

        @JvmStatic
        fun createChildren(
            executionStack: FactorioExecutionStack,
            parent: FactorioVariableContainer?,
            varResp: DAPVariablesResponse?
        ): List<FactorioVariableValue> {
            val variables = varResp?.body?.variables ?: return emptyList()
            val result: MutableList<FactorioVariableValue> = ArrayList(variables.size)
            for (variable in variables) {
                if (variable.variablesReference == 0) result.add(FactorioVariableValue(executionStack, parent, variable)) else result.add(
                    FactorioVariableValueGroup(executionStack, parent, variable)
                )
            }
            return result
        }
    }
}
