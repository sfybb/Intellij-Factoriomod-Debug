package factorio.debugger.DAP.messages.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import factorio.debugger.DAP.messages.DAPAdditionalProperties;
import factorio.debugger.DAP.messages.DAPRequest;

@JsonTypeName("disconnect")
public class DAPDisconnectRequest extends DAPRequest<DAPDisconnectRequest.DisconnectArguments> {
    public static class DisconnectArguments extends DAPAdditionalProperties {
        /**
         * A value of true indicates that this `disconnect` request is part of a
         * restart sequence.
         */
        @JsonProperty("restart")
        public boolean restart;

        /**
         * Indicates whether the debuggee should be terminated when the debugger is
         * disconnected.
         * If unspecified, the debug adapter is free to do whatever it thinks is best.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportTerminateDebuggee` is true.
         */
        @JsonProperty("terminateDebuggee")
        public boolean terminateDebuggee;

        /**
         * Indicates whether the debuggee should stay suspended when the debugger is
         * disconnected.
         * If unspecified, the debuggee should resume execution.
         * The attribute is only honored by a debug adapter if the corresponding
         * capability `supportSuspendDebuggee` is true.
         */
        @JsonProperty("suspendDebuggee")
        public boolean suspendDebuggee;
    }
}
