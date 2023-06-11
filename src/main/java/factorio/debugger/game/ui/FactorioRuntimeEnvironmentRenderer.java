package factorio.debugger.game.ui;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.Border;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.intellij.javascript.nodejs.interpreter.LeftRightJustifyingLayoutManager;
import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import factorio.debugger.game.FactorioRuntimeEnvironment;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;
import factorio.debugger.game.FactorioRuntimeEnvironmentType;
import factorio.debugger.game.FactorioVersion;

/**
 * Re-Implementation of com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRenderer for RuntimeEnvironmentField
 */
public class FactorioRuntimeEnvironmentRenderer implements ListCellRenderer<FactorioRuntimeEnvironmentRef> {
    private static final Supplier<@Nls String> NO_INTERPRETER_TEXT = JavaScriptBundle.messagePointer("node.no.interpreter");
    private final SimpleColoredComponent myNameComp;
    private final SimpleColoredComponent myVersionComp;
    private final List<FactorioRuntimeEnvironmentType<?>> myRuntimeTypes;
    private final JPanel myPanel;

    public FactorioRuntimeEnvironmentRenderer(boolean compact, final @NotNull List<FactorioRuntimeEnvironmentType<?>> runtimeTypes) {
        super();
        this.myRuntimeTypes = runtimeTypes;
        this.myNameComp = new SimpleColoredComponent();
        this.myVersionComp = new SimpleColoredComponent();
        this.myPanel = new JPanel(new GridBagLayout());

        this.myNameComp.setOpaque(false);
        this.myVersionComp.setOpaque(false);

        JBInsets insets = compact ? new JBInsets(0, 1, 0, 1) : new JBInsets(2, UIUtil.getListCellHPadding(), 2, UIUtil.getListCellHPadding());
        this.myPanel.setBorder(IdeBorderFactory.createEmptyBorder(insets));
        this.myNameComp.setIpad(JBInsets.create(0, 0));
        this.myVersionComp.setIpad(JBUI.insetsLeft(10));

        if (compact) {
            adjustBorderHeight(this.myNameComp);
            this.myVersionComp.setMyBorder((Border)null);
        }

        JPanel nameContainer = wrapInLeftRightJustifyingContainer(this.myNameComp);
        this.myPanel.add(nameContainer, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 256, 2, JBInsets.emptyInsets(), 0, 0));
        this.myPanel.add(this.myVersionComp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 256, 0, JBInsets.emptyInsets(), 0, 0));

    }

    public static void adjustBorderHeight(@NotNull SimpleColoredComponent component) {
        component.setMyBorder(JBUI.Borders.empty(1, 0));
    }

    private static @NotNull JPanel wrapInLeftRightJustifyingContainer(final @NotNull SimpleColoredComponent component) {
        JPanel container = new JPanel(new LeftRightJustifyingLayoutManager()) {
            public int getBaseline(int width, int height) {
                return component.getBaseline(width, height);
            }
        };
        container.setBorder(JBUI.Borders.empty());
        container.setOpaque(false);
        container.add(component);
        return container;
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends FactorioRuntimeEnvironmentRef> list,
                                                  final FactorioRuntimeEnvironmentRef runtimeEnvRef,
                                                  final int indx, final boolean isSelected, final boolean cellHasFocus) {
        this.myNameComp.clear();
        this.myVersionComp.clear();

        Font baseFont = list.getFont();
        this.myNameComp.setFont(baseFont);
        this.myVersionComp.setFont(UIUtil.getFont(UIUtil.FontSize.SMALL, baseFont));
        if (this.myPanel.isEnabled() != list.isEnabled()) {
            UIUtil.setEnabled(this.myPanel, list.isEnabled(), true);
        }

        if (runtimeEnvRef == null) {
            return this.myPanel;
        } else {
            this.myPanel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            Color foreground = isSelected ? list.getSelectionForeground() : list.getForeground();
            this.myNameComp.setForeground(foreground);
            this.myVersionComp.setForeground(foreground);
            this.customize(list, runtimeEnvRef, indx, isSelected);
            return this.myPanel;
        }
    }

    private void customize(@NotNull JList<? extends FactorioRuntimeEnvironmentRef> list,
                           @NotNull FactorioRuntimeEnvironmentRef runtimeEnvRef,
                           int index, boolean isSelected) {
        if (runtimeEnvRef.equals(RuntimeEnvironmentField.NO_INTERPRETER_REF)) {
            this.myNameComp.append(NO_INTERPRETER_TEXT.get(), SimpleTextAttributes.ERROR_ATTRIBUTES);
        } else {
            FactorioRuntimeEnvironment runtimeEnv = runtimeEnvRef.resolve();
            FactorioVersion version = runtimeEnv != null && myRuntimeTypes.contains(runtimeEnv.getType()) ? runtimeEnv.getVersion() : null;

            String errorMessage = null;
            /*if (versionRef != null && versionRef.isNull() && (interpreter instanceof NodeJsLocalInterpreter || interpreter instanceof WslNodeInterpreter)) {
                errorMessage = interpreter.validate((Project)null);
            }*/

            this.myPanel.setToolTipText(errorMessage);

            boolean valid = runtimeEnv != null && errorMessage == null;
            String unresolvedReferenceName;
            if (runtimeEnv != null) {
                this.myNameComp.append(runtimeEnv.getPresentableName(),
                    valid ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.ERROR_ATTRIBUTES);
            } else {
                unresolvedReferenceName = runtimeEnvRef.getReferenceName();
                this.myNameComp.append(unresolvedReferenceName + " (" +
                    JavaScriptBundle.message("node.interpreter.reference_not_found.text") + ")",
                    SimpleTextAttributes.ERROR_ATTRIBUTES);
            }

            if (version != null) {
                this.addVersion(version, isSelected);
            }
        }
    }

    private void addVersion(@NotNull FactorioVersion version, boolean isSelected) {
        SimpleTextAttributes attrs = isSelected ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.GRAY_ATTRIBUTES;
        this.myVersionComp.append(String.valueOf(version), attrs);
    }

}
