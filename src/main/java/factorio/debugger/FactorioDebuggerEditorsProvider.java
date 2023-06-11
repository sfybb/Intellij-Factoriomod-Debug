package factorio.debugger;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiPlainTextFile;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.source.PsiPlainTextFileImpl;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;

public class FactorioDebuggerEditorsProvider extends XDebuggerEditorsProvider {
    @Override
    public @NotNull FileType getFileType() {
        return FileTypeRegistry.getInstance().getFileTypeByExtension("lua");
    }


    @Override
    public @NotNull Collection<Language> getSupportedLanguages(@NotNull final Project project, @Nullable final XSourcePosition sourcePosition) {
        return Collections.singleton(Language.findLanguageByID("Lua"));
    }

    @Override
    public @NotNull Document createDocument(@NotNull Project project,
                                            @NotNull String text,
                                            @Nullable XSourcePosition sourcePosition,
                                            @NotNull EvaluationMode mode) {
        text = text.trim();
        PsiPlainTextFile fragment = new PsiPlainTextFileImpl(
            PsiManagerEx.getInstanceEx(project).getFileManager().createFileViewProvider(
                new LightVirtualFile("factorioFragment.txt", FileTypeRegistry.getInstance().getFileTypeByExtension("txt"), text),
                true
        ));
        return Objects.requireNonNull(PsiDocumentManager.getInstance(project).getDocument(fragment));
    }
}
