package factorio.debugger.frames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import factorio.debugger.DAP.messages.response.DAPScopesResponse;
import factorio.debugger.DAP.messages.response.DAPStackTraceResponse;
import factorio.debugger.DAP.messages.types.DAPCapabilitiesEnum;
import factorio.debugger.DAP.messages.types.DAPStackFrame;
import factorio.debugger.DAP.messages.types.DAPVariable;
import factorio.debugger.FactorioDebugProcess;

public class FactorioExecutionStack extends XExecutionStack {
    private Logger logger = Logger.getInstance(FactorioExecutionStack.class);
    private List<FactorioStackFrame> stackFrames;
    private Map<Integer, FactorioPartialVariableRequest> myCachedVariableChildren;
    private final FactorioDebugProcess myDebugProcess;

    public FactorioExecutionStack(String threadName, DAPStackTraceResponse.StackTraceBody stackTrace,
                                  final FactorioDebugProcess debugProcess) {
        super(threadName);

        myDebugProcess = debugProcess;
        myCachedVariableChildren = new HashMap<>();
        stackFrames = new ArrayList<>();
        if (stackTrace.stackFrames != null) {
            for (final DAPStackFrame stackFrame : stackTrace.stackFrames) {
                stackFrames.add(new FactorioStackFrame(debugProcess, this, stackFrame));
            }
        }
    }

    public @Nullable FactorioStackFrame getFrameForPosition(@NotNull final XSourcePosition position) {
        for (final FactorioStackFrame stackFrame : stackFrames) {
            XSourcePosition stackSP = stackFrame.getSourcePosition();
            if (stackSP != null &&
                Objects.equals(stackSP.getFile().getUrl(), position.getFile().getUrl()) &&
                Objects.equals(stackSP.getLine(), position.getLine())) {
                return stackFrame;
            }
        }
        return stackFrames.size() > 0 ? stackFrames.get(0) : null;
    }

    public boolean hasCapability(DAPCapabilitiesEnum cap) {
        return this.myDebugProcess.hasCapability(cap);
    }

    public Promise<List<FactorioVariableValue>> getVariableChildren(@Nullable FactorioVariableContainer parent,
                                                                    final int myContentRefId,
                                                                    final int maxResults) {
        return getVariableChildren(parent, myContentRefId, maxResults, 0);
    }

    public Promise<List<FactorioVariableValue>> getVariableChildren(@Nullable FactorioVariableContainer parent,
                                                                    final int myContentRefId,
                                                                    final int maxResults,
                                                                    final int offset) {
        if(myContentRefId == 0) return Promises.resolvedPromise(Collections.emptyList());
        FactorioPartialVariableRequest partialVarRequest = myCachedVariableChildren.get(myContentRefId);

        if (partialVarRequest == null) {
            if(offset != 0) logger.warn(String.format("New variable request has non-zero offset: %d", offset));

            partialVarRequest = new FactorioPartialVariableRequest(
                myDebugProcess.getVariable(myContentRefId, 0, maxResults)
                    .then(varResp -> FactorioVariableValue.createChildren(this, parent, varResp))
                , maxResults);

            myCachedVariableChildren.put(myContentRefId, partialVarRequest);
        } else if (!partialVarRequest.isComplete()) {
            if (partialVarRequest.numContainedVars() != offset) logger.warn(String.format("Expected offset to be %d but it is %d", partialVarRequest.numContainedVars(), offset));

            partialVarRequest.addRequest(
                myDebugProcess.getVariable(myContentRefId, offset, maxResults)
                    .then(varResp -> FactorioVariableValue.createChildren(this, parent, varResp)),
                maxResults
            );
        }

        return partialVarRequest.getVariableListPromise();
    }

    public Promise<DAPScopesResponse> getScope(final int scopeId) {
        return this.myDebugProcess.getScope(scopeId);
    }

    @Override
    public @Nullable XStackFrame getTopFrame() {
        return stackFrames.size() > 0 ? stackFrames.get(0) : null;
    }

    @Override
    public void computeStackFrames(final int firstFrameIndex, final XStackFrameContainer container) {
        if(container.isObsolete()) return;

        if (firstFrameIndex <= stackFrames.size()) {
            List<FactorioStackFrame> xFrames = new ArrayList<>();

            for(int i = firstFrameIndex; i < stackFrames.size(); ++i) {
                xFrames.add(stackFrames.get(i));
            }
            container.addStackFrames(xFrames, true);
        } else {
            container.addStackFrames(Collections.emptyList(), true);
        }
    }

    public String toJSON(final Object obj) {
        return myDebugProcess.toJSON(obj);
    }

    public Promise<DAPVariable> setValue(final @Nullable FactorioVariableContainer myParent, final @NotNull String myVariableName, final String expression) {
        return this.myDebugProcess.setValue(myParent, myVariableName, expression);
    }
}
