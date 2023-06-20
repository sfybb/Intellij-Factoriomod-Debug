package factorio.debugger.game.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.ComponentUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ListUtil;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollingUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.update.UiNotifyConnector;
import factorio.debugger.FactorioDebuggerBundle;
import factorio.debugger.game.FactorioRuntimeEnvironment;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;
import factorio.debugger.game.FactorioRuntimeEnvironmentType;

/**
 * Re-implementation of com.intellij.javascript.nodejs.interpreter.NodeJsInterpretersDialog for RuntimeEnvironmentField
 */
public class RuntimeEnvironmentsDialog extends DialogWrapper {
    private final RuntimeEnvironmentField myRuntimeEnvField;
    private final DefaultListModel<FactorioRuntimeEnvironmentRef> myListModel;
    private final JBList<FactorioRuntimeEnvironmentRef> myList;
    private final JComponent myCenterPanelComponent;
    private final Map<FactorioRuntimeEnvironmentRef, FactorioRuntimeEnvironment> myChangesMap;

    public RuntimeEnvironmentsDialog(@NotNull RuntimeEnvironmentField runtimeEnvField) {
        super((Project) null, true);
        this.myChangesMap = new HashMap<>();
        this.myRuntimeEnvField = runtimeEnvField;
        this.myListModel = new DefaultListModel<>();
        this.myList = new JBList<>(this.myListModel);
        this.myList.setSelectionMode(2);
        this.myList.getEmptyText().setText(getEmptyText());
        this.myList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = RuntimeEnvironmentsDialog.this.myList.locationToIndex(e.getPoint());
                    if (row >= 0) {
                        RuntimeEnvironmentsDialog.this.myList.setSelectedIndex(row);
                    }
                }

            }
        });
        this.myList.setCellRenderer(new FactorioRuntimeEnvironmentRenderer(false, myRuntimeEnvField.getRuntimeTypes()));
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.myList)
            .setPanelBorder(JBUI.Borders.empty()).setAddAction(this::performAddAction)
            .setRemoveAction((button) -> ListUtil.removeSelectedItems(this.myList, Objects::nonNull)).setRemoveActionUpdater((e) -> {
                FactorioRuntimeEnvironmentRef ref = this.myList.getSelectedValue();
                return ref != null;
            })
            .setEditAction((button) -> this.editSelected())
            .setEditActionUpdater((e) -> this.isEditAvailable()).disableUpDownActions();

        JPanel decoratorPanel = decorator.createPanel();
        (new DoubleClickListener() {
            protected boolean onDoubleClick(@NotNull MouseEvent event) {
                if (RuntimeEnvironmentsDialog.this.isEditAvailable()) {
                    RuntimeEnvironmentsDialog.this.editSelected();
                    return true;
                } else {
                    return false;
                }
            }
        }).installOn(this.myList);
        decoratorPanel.setPreferredSize(JBUI.size(550, 300));
        JScrollPane pane = ComponentUtil.getScrollPane(this.myList);
        if (pane != null) {
            pane.setHorizontalScrollBarPolicy(31);
        }

        this.myCenterPanelComponent = decoratorPanel;
        this.setTitle(/*JavaScriptBundle.message("node.js.interpreters")*/runtimeEnvField.getDialogTitle());
        this.init();
        this.configurePopupHandler();
        this.fillList();
    }

    @NotNull
    protected DialogWrapper.@NotNull DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    protected @Nullable JComponent createCenterPanel() {
        return this.myCenterPanelComponent;
    }

    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.myList;
    }

    private @NotNull List<FactorioRuntimeEnvironmentRef> getRuntimeRefs() {
        List<FactorioRuntimeEnvironmentRef> runtimeRefs = new ArrayList<>();

        for(int i = 0; i < this.myListModel.size(); ++i) {
            FactorioRuntimeEnvironmentRef runtimeRef = this.myListModel.elementAt(i);
            if (runtimeRef != null) {
                runtimeRefs.add(runtimeRef);
            }
        }

        return runtimeRefs;
    }

    private void performAddAction(@NotNull AnActionButton button) {
        JBPopup popup = createAddPopup(this.myRuntimeEnvField, button.getDataContext(), (runtimeEnv) -> {
            FactorioRuntimeEnvironmentRef runtimeRef = runtimeEnv.toRef();
            this.myChangesMap.put(runtimeRef, runtimeEnv);
            this.addIfNeededAndSelect(runtimeRef);
        });
        /*RelativePoint point = button.getPreferredPopupPoint();
        popup.show(point);*/
    }

    public static /*@NotNull*/ JBPopup createAddPopup(@NotNull RuntimeEnvironmentField runtimeEnvField,
                                                  @NotNull DataContext dataContext,
                                                  @NotNull Consumer<? super FactorioRuntimeEnvironment> callback) {
        /*List<FactorioRuntimeEnvironmentType<?>> types = runtimeEnvField.getRuntimeTypes();
        DefaultActionGroup group = new DefaultActionGroup();

        for (final FactorioRuntimeEnvironmentType<?> type : types) {
            group.add(new AddInterpreterTypeAction(type, callback));

        }

        return JBPopupFactory.getInstance().createActionGroupPopup(
            null,  group, dataContext, false, true,
            true, null, -1, null);*/
        List<FactorioRuntimeEnvironmentType<?>> types = runtimeEnvField.getRuntimeTypes();

        if(types.size() > 0) {
            FactorioRuntimeEnvironment addedRuntimeEnv = types.get(0).showAddDialog();
            if (addedRuntimeEnv != null) {
                callback.accept(addedRuntimeEnv);
            }
        }
        return null;
    }

    public @Nullable Ref<FactorioRuntimeEnvironmentRef> showAndGetSelected(@Nullable FactorioRuntimeEnvironmentRef runtimeToAddAndSelect) {
        if (runtimeToAddAndSelect != null && !runtimeToAddAndSelect.equals(RuntimeEnvironmentField.NO_INTERPRETER_REF)) {
            this.addIfNeededAndSelect(runtimeToAddAndSelect);
        } else {
            this.myList.clearSelection();
        }

        if (!this.showAndGet()) {
            return null;
        } else {
            MultiMap<FactorioRuntimeEnvironmentType<?>, FactorioRuntimeEnvironment> group = MultiMap.create();
            for (final FactorioRuntimeEnvironmentType<?> type : this.myRuntimeEnvField.getRuntimeTypes()) {
                group.put(type, new ArrayList<>());
            }

            List<FactorioRuntimeEnvironmentRef> runtimeRefs = this.getRuntimeRefs();

            for (final FactorioRuntimeEnvironmentRef runtimeRef : runtimeRefs) {
                FactorioRuntimeEnvironment runtimeEnv = this.myChangesMap.get(runtimeRef);
                if(runtimeEnv == null) {
                    runtimeEnv = runtimeRef.resolve();
                }

                if(runtimeEnv != null && group.keySet().contains(runtimeEnv.getType())) {
                    group.putValue(runtimeEnv.getType(), runtimeEnv);
                }
            }

            for (final Map.Entry<FactorioRuntimeEnvironmentType<?>, Collection<FactorioRuntimeEnvironment>> entry : group.entrySet()) {
                entry.getKey().setEnvironments(new ArrayList<>(entry.getValue()));
            }

            return Ref.create(this.myList.getSelectedValue());
        }
    }

    private void addIfNeededAndSelect(@NotNull FactorioRuntimeEnvironmentRef runtimeRef) {
        int index = this.myListModel.indexOf(runtimeRef);
        if (index == -1) {
            this.myListModel.addElement(runtimeRef);
            index = this.myListModel.size() - 1;
        }

        this.myList.setSelectedIndex(index);
        final int finalIndex = index;
        UiNotifyConnector.doWhenFirstShown(this.myList, () -> ScrollingUtil.ensureIndexIsVisible(this.myList, finalIndex, 1));
    }

    private void configurePopupHandler() {
        List<AnAction> actionList = new ArrayList<>();

        DefaultActionGroup actionGroup = new DefaultActionGroup(actionList);
        for (final AnAction action : actionList) {
            action.registerCustomShortcutSet(action.getShortcutSet(), this.myList);
        }

        PopupHandler.installPopupMenu(this.myList, actionGroup, "NodeJSInterpreterListPopup");
    }

    private @Nullable FactorioRuntimeEnvironmentRef getSingleSelectedInterpreterRef() {
        return this.myList.getMinSelectionIndex() != this.myList.getMaxSelectionIndex() ? null : this.myList.getSelectedValue();
    }

    private boolean isEditAvailable() {
        FactorioRuntimeEnvironmentRef runtimeRef = this.getSingleSelectedInterpreterRef();
        return runtimeRef != null;
    }

    private void editSelected() {
        int selectedInd = this.myList.getSelectedIndex();
        if (selectedInd >= 0) {
            FactorioRuntimeEnvironmentRef runtimeRef = this.myListModel.getElementAt(selectedInd);
            if (runtimeRef != null) {
                FactorioRuntimeEnvironment runtimeEnv = runtimeRef.resolve();
                if (runtimeEnv == null) {
                    return;
                }

                final FactorioRuntimeEnvironmentType<?> runtimeType = runtimeEnv.getType();
                FactorioRuntimeEnvironment newRuntimeEnv = runtimeType.edit(runtimeEnv);
                if (newRuntimeEnv != null) {
                    FactorioRuntimeEnvironmentRef newRuntimeRef = newRuntimeEnv.toRef();
                    int index = this.myListModel.indexOf(newRuntimeRef);
                    if (index != -1 && index != selectedInd) {
                        this.myList.setSelectedIndex(index);
                        selectedInd = index;
                    }

                    this.myListModel.set(selectedInd, newRuntimeRef);
                    this.myChangesMap.put(newRuntimeRef, newRuntimeEnv);
                }
            }
        }

    }

    private void fillList() {
        List<FactorioRuntimeEnvironmentRef> refsList = this.myRuntimeEnvField.createRuntimeRefList();
        for (final FactorioRuntimeEnvironmentRef ref : refsList) {
            this.myListModel.addElement(ref);
        }
    }

    private static @NotNull @NlsContexts.StatusText String getEmptyText() {
        ShortcutSet shortcutSet = CommonActionsPanel.getCommonShortcut(CommonActionsPanel.Buttons.ADD);
        Shortcut shortcut = ArrayUtil.getFirstElement(shortcutSet.getShortcuts());

        return shortcut != null ? FactorioDebuggerBundle.message("environment.status.add.with", KeymapUtil.getShortcutText(shortcut)):
            FactorioDebuggerBundle.message("environment.nothing.added");
    }

    private static class AddInterpreterTypeAction extends DumbAwareAction {
        private final FactorioRuntimeEnvironmentType<?> myType;
        private final Consumer<? super FactorioRuntimeEnvironment> myCallback;

        AddInterpreterTypeAction(@NotNull FactorioRuntimeEnvironmentType<?> type, @NotNull Consumer<? super FactorioRuntimeEnvironment> callback) {
            super(FactorioDebuggerBundle.message("environment.add.action", StringUtil.capitalize(type.getName())));
            this.myType = type;
            this.myCallback = callback;
        }

        public void actionPerformed(@NotNull AnActionEvent e) {
            FactorioRuntimeEnvironment addedRuntimeEnv = this.myType.showAddDialog();
            if (addedRuntimeEnv != null) {
                this.myCallback.accept(addedRuntimeEnv);
            }
        }

        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }

        public void update(@NotNull AnActionEvent e) {
            boolean available = this.myType.isAvailable();
            e.getPresentation().setEnabledAndVisible(available);
        }
    }
}
