package factorio.debugger.runconfig;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.TextFieldWithAutoCompletion;
import factorio.debugger.game.FactorioFMTKRuntimeEnvironmentType;
import factorio.debugger.game.FactorioGameRuntimeEnvironmentType;
import factorio.debugger.game.FactorioRuntimeEnvironmentRef;
import factorio.debugger.game.ui.RuntimeEnvironmentField;

public class FactorioSettingsEditor extends SettingsEditor<FactorioRunConfiguration> {
    private JPanel myPanel;
    private LabeledComponent<TextFieldWithAutoCompletion<String>> myFactorioDebugArgs;
    private LabeledComponent<RuntimeEnvironmentField> myFMTKPackagePath;

    private LabeledComponent<RuntimeEnvironmentField> myFactorioGame;
    private LabeledComponent<NodeJsInterpreterField> myNodeJsInterpreter;
    private JCheckBox enableTraceCheckBox;
    private LabeledComponent<JTextField> myOtherNodeArguments;
    private final Project myProject;

    public FactorioSettingsEditor(@NotNull final Project project) {
        myProject = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull final FactorioRunConfiguration s) {
        FactorioRunConfigurationOptions opt = s.getOptions();

        myFactorioGame.getComponent().setRuntimeRef(new FactorioRuntimeEnvironmentRef(opt.getFactorioRuntimeRef()));
        myFMTKPackagePath.getComponent().setRuntimeRef(new FactorioRuntimeEnvironmentRef(opt.getFMTKPackage()));
        myNodeJsInterpreter.getComponent().setInterpreterRef(NodeJsInterpreterRef.create(opt.getNodeJsInterpreterRef()));
        enableTraceCheckBox.setSelected(opt.getTrace());
        myOtherNodeArguments.getComponent().setText(opt.getDebugNodeArgs());

    }

    @Override
    protected void applyEditorTo(@NotNull final FactorioRunConfiguration s) throws ConfigurationException {
        FactorioRunConfigurationOptions opt = s.getOptions();
        opt.setFactorioRuntimeRef(myFactorioGame.getComponent().getRuntimeRef().getReferenceName());
        opt.setFMTKPackage(myFMTKPackagePath.getComponent().getRuntimeRef().getReferenceName());
        opt.setNodeJsInterpreterRef(myNodeJsInterpreter.getComponent().getInterpreterRef().getReferenceName());
        opt.setTrace(enableTraceCheckBox.isSelected());
        opt.setDebugNodeArgs(myOtherNodeArguments.getComponent().getText());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        //logger.warn("Creating editor ui... " + myPanel);
        return myPanel;
    }

    private void createUIComponents() throws ConfigurationException {
        myOtherNodeArguments = new LabeledComponent<>();
        myOtherNodeArguments.setComponent(new JTextField());

        //logger.warn("Building UI components...");
        myFMTKPackagePath = new LabeledComponent<>();
        myFMTKPackagePath.setComponent(new RuntimeEnvironmentField(FactorioFMTKRuntimeEnvironmentType.getInstance()));

        myNodeJsInterpreter = new LabeledComponent<>();
        myNodeJsInterpreter.setComponent(new NodeJsInterpreterField(myProject));

        myFactorioGame = new LabeledComponent<>();
        myFactorioGame.setComponent(new RuntimeEnvironmentField(FactorioGameRuntimeEnvironmentType.getInstance()));
        /*myFactorioPath.getComponent().addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false,
            false, false, false) {
            @Override
            public boolean isFileVisible(final VirtualFile file, final boolean showHiddenFiles) {
                return (!file.is(VFileProperty.HIDDEN) || showHiddenFiles) && (file.isDirectory() || isFileSelectable(file));
            }

            @Override
            public boolean isFileSelectable(@Nullable final VirtualFile file) {
                if (!super.isFileSelectable(file) || !file.isInLocalFileSystem())
                    return false;

                return (!SystemInfo.isWindows || ("exe".equals(file.getExtension()) && file.exists() && !file.isDirectory())) && (!SystemInfo.isMac || ("app".equals(file.getExtension()) && file.isDirectory())) && (!SystemInfo.isLinux || Files.isExecutable(Paths.get(file.getPath())));
            }
        }));*/
        myFactorioDebugArgs = new LabeledComponent<>();

        //Collection<String> availableArgs = FMTKInfoProvider.getInstance(myProject).getAvailableArguments();

        /*myFactorioDebugArgs.setComponent(new TextFieldWithAutoCompletion(myProject,
            new TextFieldWithAutoCompletionListProvider(availableArgs) {
                @Override
                protected @NotNull String getLookupString(@NotNull final Object item) {
                    return String.valueOf(item);
                }
            }, true, null));*/
    }
}
