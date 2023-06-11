package factorio.debugger;

import static org.jetbrains.concurrency.Promises.collectResults;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import factorio.debugger.DAP.messages.response.DAPStackTraceResponse;
import factorio.debugger.DAP.messages.types.DAPThread;
import factorio.debugger.frames.FactorioExecutionStack;
import kotlin.Pair;

public class FactorioSuspendContext extends XSuspendContext {
    private XExecutionStack[] executionStacks;

    protected FactorioSuspendContext(List<Pair<DAPStackTraceResponse, DAPThread>> stacks, FactorioDebugProcess debugProcess) {
        executionStacks = new FactorioExecutionStack[stacks.size()];

        for (int i = 0; i < executionStacks.length; i++) {
            final Pair<DAPStackTraceResponse, DAPThread> stack = stacks.get(i);
            String threadName = stack.component2().name;
            executionStacks[i] = new FactorioExecutionStack(threadName, stack.component1().body, debugProcess);
        }
    }

    public static Promise<XSuspendContext> fromThreads(final DAPThread[] threads, final FactorioDebugProcess debugProcess) {
        List<Promise<Pair<DAPStackTraceResponse, DAPThread>>> stackTraces = new ArrayList<>(threads.length);

        for(DAPThread thread: threads) {
            stackTraces.add(debugProcess.getStackTrace(thread.id).then(st -> new Pair<>(st, thread)));
        }

        return collectResults(stackTraces).then(resultList -> new FactorioSuspendContext(resultList, debugProcess));
    }

    @Override
    public @Nullable XExecutionStack getActiveExecutionStack() {
        return this.executionStacks.length > 0 ? this.executionStacks[0] : null;
    }
}
