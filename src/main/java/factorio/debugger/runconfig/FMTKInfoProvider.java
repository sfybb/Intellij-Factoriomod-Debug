package factorio.debugger.runconfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.javascript.nodejs.interpreter.NodeCommandLineConfigurator;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreterManager;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;

public class FMTKInfoProvider {
    private static final String FMTK_ID = "factoriomod-debug";
    private static FMTKInfoProvider instance;
    private ObjectMapper myObjectMapper;
    private NodePackage myFmtkPackage;
    private PackageJsonData myPackageJson;
    private Collection<String> launchProperties;

    protected FMTKInfoProvider(@NotNull Project project) {
        myObjectMapper = new ObjectMapper();
        launchProperties = new HashSet<>();
        loadFMTKPackageInfo(project);
    }

    private void loadFMTKPackageInfo(@NotNull Project project) {
        final List<NodeJsLocalInterpreter> interpreters = NodeJsLocalInterpreterManager.getInstance().getInterpreters();
        this.myFmtkPackage = NodePackage.findDefaultPackage(project, FMTK_ID, ContainerUtil.getFirstItem(interpreters));
        if(this.myFmtkPackage == null) return;

        Path packageDir = Path.of(this.myFmtkPackage.getSystemDependentPath());
        VirtualFile vf = LocalFileSystem.getInstance().findFileByNioFile(packageDir.resolve("package.json"));
        if(vf != null) {
            myPackageJson = PackageJsonData.getOrCreate(vf);

            try {
                JsonNode packageJson = myObjectMapper.readTree(packageDir.resolve("package.json").toFile());
                processPackageJson(packageJson);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            myPackageJson = null;
        }


    }

    public static FMTKInfoProvider getInstance(@NotNull Project project) {
        if (instance == null) instance = new FMTKInfoProvider(project);
        return instance;
    }

    public @Nullable GeneralCommandLine createCommandLine(@NotNull List<String> arguments) throws ExecutionException {
        final List<NodeJsLocalInterpreter> interpreters = NodeJsLocalInterpreterManager.getInstance().getInterpreters();
        NodeJsLocalInterpreter interpreter = ContainerUtil.getFirstItem(interpreters);
        if(interpreter == null) return null;

        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
        commandLine.setExePath(interpreter.getInterpreterSystemIndependentPath());

        commandLine.addParameter(this.getExecutable());
        commandLine.addParameters(arguments);

        NodeCommandLineConfigurator.find(interpreter).configure(commandLine);
        return commandLine;
    }

    private @NotNull String getExecutable() {
        return Path.of(myFmtkPackage.getSystemDependentPath(), myPackageJson.getMain() == null ? "" : myPackageJson.getMain()).normalize().toString();
    }

    public Collection<String> getAvailableArguments() {
        return launchProperties;
    }

    private void processPackageJson(@NotNull JsonNode packageJson) {
        this.launchProperties = Collections.emptySet();

        // Sanity check
        if (!"justarandomgeek".equals(packageJson.at("/publisher").asText("")) ||
            !"factoriomod-debug".equals(packageJson.at("/name").asText(""))) {
            // Wrong package!
            return;
        }

        JsonNode debuggerConfig = packageJson.at("/contributes/debuggers/0");
        if(!debuggerConfig.isContainerNode()) {
            return;
        }
        JsonNode launchProperties = debuggerConfig.at("/configurationAttributes/launch/properties");
        if(!launchProperties.isContainerNode()) return;
        this.launchProperties = getLaunchArgs(launchProperties);
    }

    private Collection<String> getLaunchArgs(final @NotNull JsonNode launchPreferences) {
        Collection<String> result = new HashSet<>();

        Iterator<Map.Entry<String, JsonNode>> fields = launchPreferences.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            result.add(field.getKey());
        }
        return result;
    }

    /*public class FMTKLaunchProperty {
        private final @NotNull ArgumentType type;
        private final @Nullable FMTKLaunchProperty[] children;

        private final @NotNull String name;
    }*/

    public enum ArgumentType {
        BOOLEAN,
        STRING,
        INTEGER,
        ENUM,
        ARRAY,
        ONEOF,
        ANY;
    }
}
