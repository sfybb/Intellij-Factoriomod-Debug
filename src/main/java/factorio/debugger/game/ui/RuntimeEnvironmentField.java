package factorio.debugger.game.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import org.jetbrains.annotations.NotNull;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.PopupMenuListenerAdapter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.SwingHelper;
import factorio.debugger.FactorioDebuggerBundle;
import factorio.debugger.game.FactorioRuntimeEnvironment;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;
import factorio.debugger.game.FactorioRuntimeEnvironmentType;

/**
 * Selection field for the Factorio Game and FMTK executeables
 * Modeled after com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
 * Since this class only works with NodeJsInterpreters I recreated it to work with the Factorio executeables
 * This is done to keep the UI familiar for the user
 */
public class RuntimeEnvironmentField extends ComponentWithBrowseButton<ComboBox<FactorioRuntimeEnvironmentRef>> {
    private static final Logger LOG = Logger.getInstance(RuntimeEnvironmentField.class);
    private static final FactorioRuntimeEnvironmentRef ADD_INTERPRETER = FactorioRuntimeEnvironmentRef.create("#Add interpreter");
    static final FactorioRuntimeEnvironmentRef NO_INTERPRETER_REF = FactorioRuntimeEnvironmentRef.create("");

    private final FactorioRuntimeEnvironmentType<?> myRuntimeEnvironmentType;
    private final KeyEventAwareComboBox myComboBox;
    private final RuntimeComboBoxModel myModel;
    private final List<RuntimeEnvironmentChangeListener> myChangeListeners;
    private FactorioRuntimeEnvironmentRef myLastSelectedItem;
    private boolean myDropDownListUpdateRequested;


    public RuntimeEnvironmentField(FactorioRuntimeEnvironmentType<?> runtimeEnvironmentType) {
        super(new KeyEventAwareComboBox(), null);
        this.myChangeListeners = ContainerUtil.createLockFreeCopyOnWriteList();
        this.myLastSelectedItem = NO_INTERPRETER_REF;
        this.myDropDownListUpdateRequested = false;
        this.addActionListener((e) -> {
            this.showInterpretersDialog();
        });

        myRuntimeEnvironmentType = runtimeEnvironmentType;

        this.myComboBox = (KeyEventAwareComboBox)this.getChildComponent();
        this.myModel = new RuntimeComboBoxModel();
        this.myComboBox.setModel(this.myModel);
        this.myComboBox.setRenderer(new DelegatingListCellRenderer());
        this.myComboBox.setMinimumAndPreferredWidth(0);
        this.myModel.addElement(NO_INTERPRETER_REF);
        this.myModel.setSelectedItem(NO_INTERPRETER_REF);

        this.requestDropDownListUpdate();
        this.myComboBox.addItemListener((e) -> {
            if (e.getStateChange() == 1) {
                this.handleSelectedItemChange();
            }
        });
    }

    private void handleSelectedItemChange() {
        FactorioRuntimeEnvironmentRef selectedItem = this.getRuntimeRef();
        if (selectedItem == ADD_INTERPRETER) {
            this.myComboBox.setSelectedItem(this.myLastSelectedItem);
            if (!this.myComboBox.myKeyEventProcessing) {
                ApplicationManager.getApplication().invokeLater(this::showPopup, ModalityState.current());
            }
        } else if (this.myLastSelectedItem != selectedItem) {
            this.myLastSelectedItem = selectedItem;
            for (final RuntimeEnvironmentChangeListener listener : myChangeListeners) {
                listener.runtimeEnvironmentChanged(selectedItem);
            }
        }
    }

    private void showPopup() {
        JBPopup popup = RuntimeEnvironmentsDialog.createAddPopup(this, DataManager.getInstance().getDataContext(this.myComboBox),
            (runtimeEnv) -> {
            FactorioRuntimeEnvironmentType<FactorioRuntimeEnvironment> type = (FactorioRuntimeEnvironmentType<FactorioRuntimeEnvironment>) runtimeEnv.getType();
            List<FactorioRuntimeEnvironment> runtimeEnvironments = new ArrayList<>(type.getEnvironments());
            if (!runtimeEnvironments.contains(runtimeEnv)) {
                runtimeEnvironments.add(runtimeEnv);
                type.setEnvironments(runtimeEnvironments);
            }

            this.setRuntimeRef(runtimeEnv.toRef());
            this.requestDropDownListUpdate();
        });
        //popup.showUnderneathOf(this.myComboBox);
    }

    public void addChangeListener(@NotNull RuntimeEnvironmentChangeListener listener) {
        this.myChangeListeners.add(listener);
    }

    private void showInterpretersDialog() {
        RuntimeEnvironmentsDialog dialog = new RuntimeEnvironmentsDialog(this);
        Ref<FactorioRuntimeEnvironmentRef> value = dialog.showAndGetSelected((FactorioRuntimeEnvironmentRef)this.myModel.getSelectedItem());
        if (value != null) {
            if (value.get() != null) {
                this.setRuntimeRef(value.get());
            }

            this.myModel.repaintSelectedElementIfMatches(this.getRuntimeRef());
            this.requestDropDownListUpdate();
        }

    }

    private void requestDropDownListUpdate() {
        if (!this.myDropDownListUpdateRequested) {
            this.myDropDownListUpdateRequested = true;
            this.myComboBox.addPopupMenuListener(new PopupMenuListenerAdapter() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    RuntimeEnvironmentField.this.myComboBox.removePopupMenuListener(this);
                    RuntimeEnvironmentField.this.updateDropDownList();
                    RuntimeEnvironmentField.this.myDropDownListUpdateRequested = false;
                }
            });
        }

    }

    private void updateDropDownList() {
        List<FactorioRuntimeEnvironmentRef> refList = myRuntimeEnvironmentType.getEnvironmentRefs();

        FactorioRuntimeEnvironmentRef selectedRuntimeRef = this.getRuntimeRef();
        if (!refList.contains(selectedRuntimeRef)) {
            refList.add(selectedRuntimeRef);
        }

        this.myComboBox.doWithItemStateChangedEventsDisabled(() -> {
            SwingHelper.updateItems(this.myComboBox, refList, null);
            this.myComboBox.addItem(ADD_INTERPRETER);
        });

        Object selectedItem = this.myComboBox.getSelectedItem();
        if (!Objects.equals(selectedItem,  selectedRuntimeRef)) {
            LOG.warn("Actual selected item:" + selectedItem + ", expected: " + selectedRuntimeRef);
            this.myComboBox.setSelectedItem(selectedRuntimeRef);
            this.handleSelectedItemChange();
        }
    }

    public void setRuntimeRef(@NotNull FactorioRuntimeEnvironmentRef runtimeRef) {
        this.myModel.setSelectedItem(runtimeRef);
    }
    public @NotNull FactorioRuntimeEnvironmentRef getRuntimeRef() {
        FactorioRuntimeEnvironmentRef interpreterRef = (FactorioRuntimeEnvironmentRef)this.myComboBox.getSelectedItem();
        if (interpreterRef == null) {
            LOG.warn("No interpreter ref");
            interpreterRef = NO_INTERPRETER_REF;
        }
        return interpreterRef;
    }

    public List<FactorioRuntimeEnvironmentType<?>> getRuntimeTypes() {
        return List.of(this.myRuntimeEnvironmentType);
    }

    public List<FactorioRuntimeEnvironmentRef> createRuntimeRefList() {
        return this.myRuntimeEnvironmentType.getEnvironmentRefs();
    }

    public String getDialogTitle() {
        return this.myRuntimeEnvironmentType.getChooserDialogTitle();
    }

    static class RuntimeComboBoxModel extends DefaultComboBoxModel<FactorioRuntimeEnvironmentRef> {
        RuntimeComboBoxModel() {
        }

        void repaintSelectedElementIfMatches(@NotNull FactorioRuntimeEnvironmentRef expectedSelectedElement) {
            if (this.getSelectedItem() == expectedSelectedElement) {
                this.fireContentsChanged(this, -1, -1);
            }
        }
    }
    private class DelegatingListCellRenderer implements ListCellRenderer<FactorioRuntimeEnvironmentRef> {
        private final AddRuntimeRenderer myAddRuntimeRenderer = new AddRuntimeRenderer();
        private final FactorioRuntimeEnvironmentRenderer myRuntimeRenderer;

        DelegatingListCellRenderer() {
            this.myRuntimeRenderer = new FactorioRuntimeEnvironmentRenderer(true, RuntimeEnvironmentField.this.getRuntimeTypes());
        }

        public Component getListCellRendererComponent(JList<? extends FactorioRuntimeEnvironmentRef> list, FactorioRuntimeEnvironmentRef value, int index, boolean isSelected, boolean cellHasFocus) {
            ListCellRenderer<FactorioRuntimeEnvironmentRef> renderer = value == ADD_INTERPRETER ? this.myAddRuntimeRenderer : this.myRuntimeRenderer;
            list.setEnabled(RuntimeEnvironmentField.this.isEnabled());
            return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    private static class AddRuntimeRenderer extends ColoredListCellRenderer<FactorioRuntimeEnvironmentRef> {
        AddRuntimeRenderer() {
            this.setIpad(JBUI.insetsLeft(2));
            //NodeJsInterpreterRenderer.adjustBorderHeight(this);
        }

        protected void customizeCellRenderer(@NotNull JList<? extends FactorioRuntimeEnvironmentRef> list, FactorioRuntimeEnvironmentRef value, int index, boolean selected, boolean hasFocus) {
            if (value == ADD_INTERPRETER) {
                this.append(" " + FactorioDebuggerBundle.message("environment.field.add.item"), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.darkGray));
            } else {
                this.append(FactorioDebuggerBundle.message("environment.field.unexpected_value", value), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
            }

        }
    }
    private static class KeyEventAwareComboBox extends ComboBox<FactorioRuntimeEnvironmentRef> {
        private boolean myKeyEventProcessing = false;
        private boolean myItemStateChangedEventsAllowed = true;

        private KeyEventAwareComboBox() {
        }

        public void processKeyEvent(KeyEvent e) {
            this.myKeyEventProcessing = true;
            super.processKeyEvent(e);
            this.myKeyEventProcessing = false;
        }

        protected void selectedItemChanged() {
            if (this.myItemStateChangedEventsAllowed) {
                super.selectedItemChanged();
            }

        }

        public void doWithItemStateChangedEventsDisabled(@NotNull Runnable runnable) {
            this.myItemStateChangedEventsAllowed = false;
            try {
                runnable.run();
            } finally {
                this.myItemStateChangedEventsAllowed = true;
            }
        }
    }
}
