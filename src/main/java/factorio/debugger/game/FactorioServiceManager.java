package factorio.debugger.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.ImmutableList;
import com.intellij.javascript.nodejs.CompletionModuleInfo;
import com.intellij.javascript.nodejs.NodeModuleSearchUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.ContainerUtil;

@Service
@State(name = "FactorioFMTKInstances", storages = {@Storage("factorio-debugger.xml")} , category = SettingsCategory.TOOLS)
public class FactorioServiceManager implements PersistentStateComponent<Element>, Disposable {
    private static final Logger LOG = Logger.getInstance(FactorioServiceManager.class);

    private static final String FMTK_ID = "factoriomod-debug";
    private static final String EXECUTABLES_TAG_NAME = "exectuables";
    protected static final String PATH_ATTR_NAME = "path";
    protected static final String VERSION_ATTR_NAME = "version";
    protected volatile List<FactorioRuntimeEnvironment> myFactorioExecutables;


    public static FactorioServiceManager getInstance() {
        return ApplicationManager.getApplication().getService(FactorioServiceManager.class);
    }

    @Override
    public void dispose() {
    }

    @Override
    public @Nullable Element getState() {
        List<FactorioRuntimeEnvironment> executables = this.myFactorioExecutables;;
        if (executables == null) return null;

        Element parent = new Element(EXECUTABLES_TAG_NAME);

        for (final FactorioRuntimeEnvironment executable : executables) {
            LOG.debug(String.format("Adding %s with path '%s'", executable.getType().getID(), executable.getSystemIndependentPath()));
            Element executableElement = new Element(executable.getType().getID());
            executableElement.setAttribute(PATH_ATTR_NAME, executable.getSystemIndependentPath());

            /*FactorioVersion version = executable.getVersion();
            if(version != null) {
                executableElement.setAttribute(VERSION_ATTR_NAME, version.toString());
            }*/
            parent.addContent(executableElement);
        }

        return parent;
    }

    @Override
    public void loadState(@NotNull final Element state) {
        List<FactorioRuntimeEnvironment> executables = new ArrayList<>();

        List<FactorioRuntimeEnvironmentType<?>> typeCreationList = new ArrayList<>();

        typeCreationList.add(FactorioFMTKRuntimeEnvironmentType.getInstance());
        typeCreationList.add(FactorioGameRuntimeEnvironmentType.getInstance());

        for (final FactorioRuntimeEnvironmentType<?> type : typeCreationList) {
            List<Element> children = state.getChildren(type.getID());

            for (final Element child : children) {
                String executablePath = child.getAttributeValue(PATH_ATTR_NAME);
                if (executablePath != null) {
                    executablePath = FileUtil.toSystemIndependentName(executablePath);
                    /*String versionStr = child.getAttributeValue(VERSION_ATTR_NAME);
                    if (versionStr != null) {
                        FactorioVersion version = FactorioVersion.tryParse(versionStr);
                        if(version != null) {
                        }
                    }*/

                    executables.add(type.create(executablePath));
                }
            }

        }
        setExecutables(executables);
    }

    public @NotNull List<FactorioRuntimeEnvironment> getExecutables() {
        Set<String> fmtkDetectedPaths = new HashSet<>(this.getFMTKDetectedExecutablePaths());
        List<FactorioRuntimeEnvironment> result = new ArrayList<>(ContainerUtil.notNullize(this.myFactorioExecutables));

        for (final FactorioRuntimeEnvironment runtime : result) {
            fmtkDetectedPaths.remove(runtime.getSystemIndependentPath());
        }

        for (final String detectedPath : fmtkDetectedPaths) {
            result.add(new FactorioFMTKRuntimeEnvironment(detectedPath));
        }

        setExecutables(result);
        return result;
    }

    public void setExecutables(@NotNull List<FactorioRuntimeEnvironment> executables) {
        this.myFactorioExecutables = ImmutableList.copyOf(executables);
    }

    protected @NotNull Set<String> getFMTKDetectedExecutablePaths() {
        Set<String> results = new HashSet<>();

        List<CompletionModuleInfo> globalModules = new ArrayList<>();
        NodeModuleSearchUtil.findGloballyInstalledModules(globalModules, FMTK_ID, null);

        for (final CompletionModuleInfo moduleInfo : globalModules) {
            String modPath = moduleInfo.getAbsolutePath();
            if(modPath != null && FMTKUtil.checkForFMTK(modPath)) {
                results.add(modPath);
            }
        }

        return results;
    }
}
