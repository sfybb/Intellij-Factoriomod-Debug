package factorio.debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.stepping.XSmartStepIntoHandler;
import factorio.debugger.DAP.messages.types.DAPStepInTarget;
import factorio.debugger.frames.FactorioExecutionStack;
import factorio.debugger.frames.FactorioStackFrame;

public class FactorioSmartStepIntoHandler extends XSmartStepIntoHandler<FactorioSmartStepIntoVariant> {
    private final FactorioDebugProcess myDebugProcess;

    public FactorioSmartStepIntoHandler(final FactorioDebugProcess debugProcess) {
        this.myDebugProcess = debugProcess;
    }

    @Override
    public @NotNull List<FactorioSmartStepIntoVariant> computeSmartStepVariants(@NotNull final XSourcePosition position) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull Promise<List<FactorioSmartStepIntoVariant>> computeSmartStepVariantsAsync(@NotNull final XSourcePosition position) {
        XSuspendContext sctx = this.myDebugProcess.getSession().getSuspendContext();
        if(!(sctx instanceof final FactorioSuspendContext suspendContext))
            return Promises.resolvedPromise(Collections.emptyList());
        if(!(suspendContext.getActiveExecutionStack() instanceof final FactorioExecutionStack executionStack))
            return Promises.resolvedPromise(Collections.emptyList());

        FactorioStackFrame stackFrame = executionStack.getFrameForPosition(position);

        if(stackFrame == null) return Promises.resolvedPromise(Collections.emptyList());

        return this.myDebugProcess.getStepIntoTargets(stackFrame)
            .then(stepTargetsResponse -> {
                if (stepTargetsResponse == null || stepTargetsResponse.body == null ||
                    stepTargetsResponse.body.targets == null) return Collections.emptyList();
                DAPStepInTarget[] targets = stepTargetsResponse.body.targets;

                List<FactorioSmartStepIntoVariant> result = new ArrayList<>(targets.length);

                for (final DAPStepInTarget target : targets) {
                    result.add(new FactorioSmartStepIntoVariant(target, position.getFile()));
                }

                return result;
            });
    }

    @Override
    public @NotNull Promise<List<FactorioSmartStepIntoVariant>> computeStepIntoVariants(@NotNull final XSourcePosition position) {
        return computeSmartStepVariantsAsync(position);
    }

    @Override
    public void startStepInto(@NotNull final FactorioSmartStepIntoVariant variant) {
        this.myDebugProcess.stepInto(variant);
    }

    @Override
    public @NlsContexts.PopupTitle String getPopupTitle(@NotNull final XSourcePosition position) {
        return null;
    }
}
