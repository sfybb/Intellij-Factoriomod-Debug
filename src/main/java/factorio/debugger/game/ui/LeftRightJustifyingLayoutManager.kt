package factorio.debugger.game.ui

import java.awt.*

/**
 * This fulfills the same function as
 * com.intellij.javascript.nodejs.interpreter.LeftRightJustifyingLayoutManager
 * but it isn't bound to the javascript plugin
 */
class LeftRightJustifyingLayoutManager : LayoutManager {
    override fun addLayoutComponent(s: String, component: Component) {}
    override fun removeLayoutComponent(component: Component) {}

    override fun preferredLayoutSize(container: Container): Dimension {
        return addInsetToDim(container.insets,
            if (container.componentCount > 0) container.getComponent(0).minimumSize
            else Dimension())
    }

    override fun minimumLayoutSize(container: Container): Dimension {
        return addInsetToDim(container.insets,
            if (container.componentCount > 0) container.getComponent(0).preferredSize
            else Dimension())
    }

    fun addInsetToDim(inset: Insets, dim: Dimension): Dimension {
        dim.width += inset.left + inset.right
        dim.height += inset.top + inset.bottom
        return dim
    }

    override fun layoutContainer(container: Container) {
        val firstChild = if (container.componentCount > 0) container.getComponent(0) else null
        firstChild?.let {
            val prefSize = it.preferredSize
            val inset = container.insets
            val availableWidth = container.width - inset.left - inset.right
            val x = if (prefSize.width < availableWidth) inset.left
            else availableWidth - prefSize.width + inset.left

            it.setBounds(x, inset.top, prefSize.width, prefSize.height)
        }
    }
}
