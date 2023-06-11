package factorio.debugger.game;

import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.SystemProperties;

public interface FactorioRuntimeEnvironment {
    @NotNull FactorioRuntimeEnvironmentType<? extends FactorioRuntimeEnvironment> getType();

    @NotNull String getReferenceName();

    @NotNull String getPresentableName();

    @Nullable FactorioVersion getVersion();

    @NotNull String getSystemIndependentPath();

    boolean isValid();

    @NotNull String getExecuteablePath();

    default String getPresentablePath(@NotNull String path) {
        String userHome = FileUtil.toSystemDependentName(SystemProperties.getUserHome());
        if (!StringUtil.isEmptyOrSpaces(userHome)) {
            userHome = StringUtil.trimEnd(userHome, File.separatorChar);
            String userHomeWithSeparator = userHome + File.separatorChar;

            if(path.startsWith(userHomeWithSeparator)) {
                return "~" + path.substring(userHome.length());
            }
        }
        return path;
    }

    FactorioRuntimeEnvironmentRef toRef();
}
