package factorio.debugger.runconfig;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;
import com.intellij.util.xml.PropertyAccessor;

public class FactorioRunConfigurationOptions extends RunConfigurationOptions {
    private final StoredProperty<String> myFactorioPath = string("").provideDelegate(this, "factorioRuntimeRef");
    private final StoredProperty<String> myNodeJsInterpreter = string("").provideDelegate(this, "nodeJsInterpreterRef");
    private final StoredProperty<String> myFMTKPackagePath = string("").provideDelegate(this, "FMTKPackage");
    private final StoredProperty<Boolean> isTrace = property(false).provideDelegate(this, "trace");
    private final StoredProperty<String> myDebugNodeArgs = string("").provideDelegate(this, "debugNodeArgs");



    @PropertyAccessor("trace")
    public boolean getTrace() {
        return isTrace.getValue(this);
    }

    @PropertyAccessor("trace")
    public void setTrace(boolean trace) {
        isTrace.setValue(this, trace);
    }


    @PropertyAccessor("nodeJsInterpreterRef")
    public @NotNull String getNodeJsInterpreterRef() {
        return myNodeJsInterpreter.getValue(this);
    }

    @PropertyAccessor("nodeJsInterpreterRef")
    public void setNodeJsInterpreterRef(final @NotNull String interpreterRef) {
        if(interpreterRef.isEmpty()) return;
        myNodeJsInterpreter.setValue(this, interpreterRef);
    }

    @PropertyAccessor("FMTKPackage")
    public @NotNull String getFMTKPackage() {
        return myFMTKPackagePath.getValue(this);
    }

    @PropertyAccessor("FMTKPackage")
    public void setFMTKPackage(final @NotNull String fmtkPackagePath) {
        if(fmtkPackagePath.isEmpty()) return;
        myFMTKPackagePath.setValue(this, fmtkPackagePath);
    }


    @PropertyAccessor("factorioRuntimeRef")
    public @NotNull String getFactorioRuntimeRef() {
        return myFactorioPath.getValue(this);
    }

    @PropertyAccessor("factorioRuntimeRef")
    public void setFactorioRuntimeRef(final @NotNull String runtimeRef) {
        if(runtimeRef.isEmpty()) return;
        myFactorioPath.setValue(this, runtimeRef);
    }

    @PropertyAccessor("debugNodeArgs")
    public @NotNull String getDebugNodeArgs() {
        return myDebugNodeArgs.getValue(this);
    }

    @PropertyAccessor("debugNodeArgs")
    public void setDebugNodeArgs(@NotNull String nodeArgs) {
        if(nodeArgs.isEmpty()) return;
        myDebugNodeArgs.setValue(this, nodeArgs);
    }
}
