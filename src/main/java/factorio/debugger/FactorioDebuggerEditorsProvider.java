package factorio.debugger;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import com.intellij.openapi.fileTypes.UnknownFileType;
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
        FileTypeRegistry ftr = FileTypeRegistry.getInstance();
        FileType lua = ftr.getFileTypeByExtension("Lua");
        return !lua.getName().equals(UnknownFileType.INSTANCE.getName()) ? lua : ftr.getFileTypeByExtension("txt");
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
        FileType ft = getFileType();
        text = text.trim();
        PsiPlainTextFile fragment = new PsiPlainTextFileImpl(
            PsiManagerEx.getInstanceEx(project).getFileManager().createFileViewProvider(
                    new LightVirtualFile("factorioFragment."+ft.getDefaultExtension(), ft, text),
                true
        ));
        return Objects.requireNonNull(PsiDocumentManager.getInstance(project).getDocument(fragment));
    }
}
