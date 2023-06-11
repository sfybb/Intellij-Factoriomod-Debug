package factorio.debugger.frames;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XErrorValuePresentation;

public class FactorioErrorVariable extends XNamedValue {
    private @NotNull final String myError;
    private @Nullable final String myType;
    public FactorioErrorVariable(@NotNull String name, @NotNull String error, @Nullable String type) {
        super(name);
        this.myError = error;
        this.myType = type;
    }

    @Override
    public void computePresentation(@NotNull final XValueNode node, @NotNull final XValuePlace place) {
        Icon icon = AllIcons.Debugger.Db_primitive;

        if (myType != null) {
            switch (myType) {
                case "method", "function" -> icon = AllIcons.Nodes.Lambda;
            }
        }

        node.setPresentation(icon, new XErrorValuePresentation(myError), false);
    }
}
