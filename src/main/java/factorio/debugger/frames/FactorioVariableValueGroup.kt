package factorio.debugger.frames

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NlsSafe
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import factorio.debugger.DAP.messages.types.DAPVariable
import javax.swing.Icon

class FactorioVariableValueGroup : FactorioVariableValue, FactorioVariableContainer {
    private val myContentRefId: Int
    private val myNumChildren: Int
    private val myChildrenMap: MutableMap<String, Int>
    private var myChildrenList: MutableList<FactorioVariableValue?>?
    private var myUiChildrenNode: XValueChildrenList?

    constructor(
        executionStack: FactorioExecutionStack,
        parent: FactorioVariableContainer?,
        variable: DAPVariable
    ) : super(executionStack, parent, variable) {
        myContentRefId = variable.variablesReference
        myUiChildrenNode = null
        myNumChildren = (variable.namedVariables ?: 0) + (variable.indexedVariables ?: 0)
        myChildrenMap = HashMap()
        myChildrenList = null
    }

    constructor(
        executionStack: FactorioExecutionStack,
        parent: FactorioVariableContainer?,
        name: String,
        value: String?,
        contentRef: Int
    ) : super(executionStack, parent, name, value ?: "", null) {
        myContentRefId = contentRef
        myUiChildrenNode = null
        myNumChildren = -1
        myChildrenMap = HashMap()
        myChildrenList = null
    }

    val icon: Icon
        get() = AllIcons.Debugger.Value
    val comment: @NlsSafe String
        get() = value

    override fun computeChildren(node: XCompositeNode) {
        if (myContentRefId == 0) {
            node.addChildren(XValueChildrenList.EMPTY, true)
            return
        }

        // TODO paging
        myUiChildrenNode?.let {
            node.setAlreadySorted(true)
            val isComplete = myNumChildren == -1 || myChildrenList?.size == myNumChildren
            node.addChildren(it, isComplete)
            if (!isComplete) {
                node.tooManyChildren(myNumChildren - myChildrenList!!.size) { addChildren(node, myChildrenList!!.size) }
            }
        } ?: myExecutionStack.getVariableChildren(this, myContentRefId, MAX_CHILDREN_PER_PAGE)
            .onProcessed { childList: MutableList<FactorioVariableValue?>? ->
            myChildrenList = childList
            val childrenList = XValueChildrenList(myChildrenList!!.size)
            myUiChildrenNode = childrenList
            var indx = 0
            for (child in myChildrenList!!) {
                myChildrenMap[child!!.name] = indx++
                myUiChildrenNode!!.add(child)
            }
            doAddChildren(node, childrenList)
        }
    }

    protected fun doAddChildren(node: XCompositeNode, childrenList: XValueChildrenList?) {
        node.setAlreadySorted(true)
        if (myNumChildren == -1 || myChildrenList!!.size >= myNumChildren || myChildrenList!!.size % MAX_CHILDREN_PER_PAGE != 0) {
            node.addChildren(childrenList!!, true)
        } else {
            node.addChildren(childrenList!!, false)
            val childrenOffset = myChildrenList!!.size
            node.tooManyChildren(myNumChildren - myChildrenList!!.size) { addChildren(node, childrenOffset) }
        }
    }

    protected fun addChildren(node: XCompositeNode, offset: Int) {
        myExecutionStack.getVariableChildren(
            this,
            myContentRefId,
            MAX_CHILDREN_PER_PAGE,
            myChildrenList!!.size
        )
            .onProcessed { nextChildList: MutableList<FactorioVariableValue?>? ->
                myChildrenList = nextChildList
                val nextChildrenList = XValueChildrenList(myChildrenList!!.size)
                for (i in offset until myChildrenList!!.size) {
                    val child = myChildrenList!![i]
                    myChildrenMap[child!!.name] = i
                    myUiChildrenNode!!.add(child)
                    nextChildrenList.add(child)
                }
                doAddChildren(node, nextChildrenList)
            }
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(icon, TableValuePresentation(comment, myContentRefId, null), true)
    }

    override fun modify(variableName: String, newValue: DAPVariable): Boolean {
        val varIndex = myChildrenMap[variableName]
        if (varIndex == null || varIndex < 0 || varIndex >= myChildrenList!!.size) return false
        newValue.name = variableName
        val myNewValue = create(myExecutionStack, this, newValue)
        myChildrenList!![varIndex] = myNewValue
        return true
    }

    override fun hasChild(variableName: String): Boolean {
        return myChildrenMap.containsKey(variableName)
    }

    override fun getReferenceId(): Int {
        return myContentRefId
    }

    override fun getParent(): FactorioVariableContainer? {
        return super.myParent
    }

    companion object {
        var MAX_CHILDREN_PER_PAGE = 25
    }
}
