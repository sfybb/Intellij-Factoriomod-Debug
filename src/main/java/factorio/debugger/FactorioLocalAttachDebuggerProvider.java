package factorio.debugger;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.process.ProcessInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.attach.LocalAttachHost;
import com.intellij.xdebugger.attach.XAttachDebugger;
import com.intellij.xdebugger.attach.XAttachDebuggerProvider;
import com.intellij.xdebugger.attach.XAttachHost;

public class FactorioLocalAttachDebuggerProvider implements XAttachDebuggerProvider {
  private Logger logger = Logger.getInstance(FactorioLocalAttachDebuggerProvider.class);

  @Override
  public boolean isAttachHostApplicable(@NotNull final XAttachHost attachHost) {
    return attachHost instanceof LocalAttachHost;
  }

  @Override
  public @NotNull List<XAttachDebugger> getAvailableDebuggers(@NotNull final Project project,
                                                              @NotNull final XAttachHost attachHost,
                                                              @NotNull final ProcessInfo processInfo,
                                                              @NotNull final UserDataHolder contextHolder) {
    throw new RuntimeException();/*
    if (!StringUtil.contains(processInfo.getCommandLine(), "factorio")) {
      return Collections.emptyList();
    }
    logger.warn("Test");
    return new LinkedList<>();*/
  }
}
