package factorio.debugger.runconfig;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;

public class FactorioConfigurationFactory extends ConfigurationFactory {
  public FactorioConfigurationFactory(final ConfigurationType type) {
    super(type);
  }

  @Override
  public @NotNull RunConfiguration createTemplateConfiguration(@NotNull final Project project) {
    return new FactorioRunConfiguration(project, this, "Factorio");
  }

  @Override
  public @Nullable Class<? extends BaseState> getOptionsClass() {
    return FactorioRunConfigurationOptions.class;
  }

  @Override
  public @NotNull @NonNls String getId() {
    return "Factorio";
  }
}
