package factorio.debugger.runconfig;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import factorio.debugger.icons.FactorioIcons;

public class FactorioRunConfigurationType implements ConfigurationType {
  static final String ID = "FactorioRunConfiguration";

  @Override
  public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
    return "Factorio";
  }

  @Override
  public @Nls(capitalization = Nls.Capitalization.Sentence) String getConfigurationTypeDescription() {
    return "Run a Factorio instance";
  }

  @Override
  public Icon getIcon() {
    return FactorioIcons.FactorioGear;
  }

  @Override
  public @NotNull @NonNls String getId() {
    return ID;
  }

  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{new FactorioConfigurationFactory(this)};
  }
}
