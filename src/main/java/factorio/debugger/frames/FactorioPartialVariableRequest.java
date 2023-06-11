package factorio.debugger.frames;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;

public class FactorioPartialVariableRequest {
    private @NotNull Promise<List<FactorioVariableValue>> myListPromise;
    private int myOffset;
    private AtomicInteger myPendingVars;

    private boolean isComplete;

    public FactorioPartialVariableRequest(final @NotNull Promise<List<FactorioVariableValue>> listPromise, int numPendingVars) {
        this.myPendingVars = new AtomicInteger(numPendingVars);
        this.myListPromise = listPromise;
        this.myOffset = 0;
        this.isComplete = false;
        this.myListPromise.then(res -> adjustOffsetAndPendingVars(res, res.size(), numPendingVars));
    }

    private List<FactorioVariableValue> adjustOffsetAndPendingVars(List<FactorioVariableValue> res, int numNewVars, int numPendingVars) {
        this.myOffset = res.size();
        this.myPendingVars.addAndGet(-numPendingVars);

        if(numPendingVars != numNewVars) {
            this.isComplete = true;
        }

        return res;
    }

    public int numContainedVars() {
        return myOffset + myPendingVars.get();
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void addRequest(final @NotNull Promise<List<FactorioVariableValue>> listPromise, int numPendingVars) {
        this.myPendingVars.addAndGet(numPendingVars);
        this.myListPromise = this.myListPromise
            .thenAsync(res -> listPromise.then(additionalVars -> {
                res.addAll(additionalVars);
                adjustOffsetAndPendingVars(res, additionalVars.size(), numPendingVars);
                return res;
            }));
    }

    @NotNull Promise<List<FactorioVariableValue>> getVariableListPromise() {
        return myListPromise;
    }
}
