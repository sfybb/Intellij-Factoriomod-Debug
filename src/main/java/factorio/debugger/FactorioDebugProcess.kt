package factorio.debugger

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.openapi.actionSystem.Anchor
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.messages.MessagesService
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Pair
import com.intellij.psi.search.ExecutionSearchScopes
import com.intellij.util.containers.toArray
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.impl.XDebugSessionImpl
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil
import com.intellij.xdebugger.stepping.XSmartStepIntoHandler
import factorio.debugger.DAP.messages.DAPEventNames
import factorio.debugger.DAP.messages.events.*
import factorio.debugger.DAP.messages.events.DAPOutputEvent.OutputCategory
import factorio.debugger.DAP.messages.requests.DAPEvaluateRequest.EvalContext
import factorio.debugger.DAP.messages.responses.*
import factorio.debugger.DAP.messages.types.*
import factorio.debugger.actions.FactorioExceptionBreakpointAction
import factorio.debugger.breakpoint.FactorioLineBreakpointHandler
import factorio.debugger.frames.FactorioSourcePosition
import factorio.debugger.frames.FactorioStackFrame
import factorio.debugger.frames.FactorioVariableContainer
import factorio.debugger.game.FactorioGameRuntimeEnvironment
import kotlinx.coroutines.*
import org.jetbrains.annotations.Async
import org.jetbrains.concurrency.*

class FactorioDebugProcess(
    session: XDebugSession,
    processHandler: ProcessHandler?,
    private val myFactorioGameRuntimeEnv: FactorioGameRuntimeEnvironment,
    executionConsole: ExecutionConsole
) : XDebugProcess(session) {
    private val mySmartStepIntoHandler: FactorioSmartStepIntoHandler
    private val logger = Logger.getInstance(FactorioDebugProcess::class.java)
    private val myProcessHandler: KillableProcessHandler?
    private val myEditorsProvider: FactorioDebuggerEditorsProvider
    private val myExecutionConsole: ExecutionConsole
    private val myDebugeeConsole: ConsoleView
    private val myDebugger: FactorioDebugger
    private lateinit var myBreakpointHandlers: Array<XBreakpointHandler<*>>
    private val myPositionConverter: FactorioLocalPositionConverter
    private var runToPositionBreakpoint: Pair<Int, XSourcePosition?>?
    private val idToBreakpointMap: MutableMap<Int?, XLineBreakpoint<*>>

    /**
     * @param session pass `session` parameter of [XDebugProcessStarter.start] method to this constructor
     */
    init {
        /** TODO [com.intellij.javascript.debugger.scripts.SourceTabManager.addScript]  */
        this.myProcessHandler = processHandler as KillableProcessHandler?
        myEditorsProvider = FactorioDebuggerEditorsProvider()
        myExecutionConsole = executionConsole
        idToBreakpointMap = HashMap()
        myPositionConverter = FactorioLocalPositionConverter(session.project)
        mySmartStepIntoHandler = FactorioSmartStepIntoHandler(this)
        session.setPauseActionSupported(true)
        val scope = ExecutionSearchScopes.executionScope(session.project, session.runProfile)
        val builder = TextConsoleBuilderFactory.getInstance().createBuilder(session.project, scope)
        myDebugeeConsole = builder.console
        //this.myDebugeeConsole.addMessageFilter(new FactorioDAPJsonFilter());
        Disposer.register(executionConsole, myDebugeeConsole)
        runToPositionBreakpoint = null
        myDebugger = FactorioDebugger(this)
        if (this.myProcessHandler != null) {
            this.myProcessHandler.addProcessListener(myDebugger.socket)
            this.myProcessHandler.addProcessListener(object : ProcessListener {
                override fun startNotified(event: ProcessEvent) {}
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {}
                override fun processTerminated(event: ProcessEvent) {
                    if (!myDebugger.wasTerminationRequested() && event.exitCode != 0) {
                        val errorMessage = myDebugger.lastReceivedMessage
                        myDebugeeConsole.print(
                            "Last message from FMTK:\n$errorMessage",
                            ConsoleViewContentType.LOG_ERROR_OUTPUT
                        )
                        session.reportError("Debugee terminated unexpectedly")
                    }
                }
            })
        }
        session.addSessionListener(object : XDebugSessionListener {
            override fun sessionStopped() {
                super.sessionStopped()
            }
        })
        myDebugger.setEventHandler(DAPEventNames.STOPPED) {
            myDebugger.threads.onProcessed { threads: DAPThreadsResponse -> positionReached(it as DAPStoppedEvent, threads) }
        }
        myDebugger.setEventHandler(DAPEventNames.OUTPUT) { debugeeOutput(it) }
        myDebugger.setEventHandler(DAPEventNames.MODULE) {
            myPositionConverter.addModule(it as DAPModuleEvent, myFactorioGameRuntimeEnv)
        }
        myDebugger.setEventHandler(DAPEventNames.LOADEDSOURCE) {
            myPositionConverter.addScript(it as DAPLoadedSourceEvent)
        }
        myDebugger.setEventHandler(DAPEventNames.INITIALIZED) {
            initialize(myDebugger.capabilities, it.sequence)
        }
        myDebugger.setEventHandler(DAPEventNames.BREAKPOINT) { handleBreakpointEvent(it) }
        myDebugger.setEventHandler(DAPEventNames.TERMINATED) {
            logger.info(it.toString())
            session.stop()
        }
    }

    private fun tryAddBreakpointTypesByClassPath(breakpointHandlers: MutableList<XBreakpointHandler<*>>, breakpointIds: Array<String>) {
        for (bpId in breakpointIds) {
            val breakpointType = XBreakpointUtil.findType(bpId)
            if (breakpointType != null) {
                breakpointHandlers.add(FactorioLineBreakpointHandler(this, breakpointType.javaClass))
            } else {
                logger.info("Cannot add breakpoint type '$bpId'")
            }
        }
    }

    private fun initialize(capabilities: DAPCapabilities, receiveSequence: Int) {

        logger.info("Connected debug adapter capabilities: ${
            capabilities.capabilities.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ", ",
                transform = { it.name }
            )
        }")
        val breakpointHandlers: MutableList<XBreakpointHandler<*>> = ArrayList()
        tryAddBreakpointTypesByClassPath(breakpointHandlers, arrayOf("lua-line", "javascript"))
        myBreakpointHandlers = breakpointHandlers.toArray(XBreakpointHandler.EMPTY_ARRAY)


        myDebugger.whenPreviousEventsProcessed(receiveSequence).onProcessed { _: List<Boolean?>? ->
            logger.warn("Requesting breakpoint initialization")
            ApplicationManager.getApplication().runReadAction {
                this@FactorioDebugProcess.session.initBreakpoints()
                myDebugger.processAddedBreakpoints(true)
                if (hasCapability(DAPCapabilitiesEnum.ConfigurationDoneRequest)) {
                    myDebugger.configurationDone()
                }
            }
        }
    }

    val capabilities: DAPCapabilities
        get() = myDebugger.capabilities

    fun hasCapability(capability: DAPCapabilitiesEnum?): Boolean {
        return capabilities.has(capability!!)
    }

    override fun registerAdditionalActions(leftToolbar: DefaultActionGroup, topToolbar: DefaultActionGroup, settings: DefaultActionGroup) {
        leftToolbar.addAction(FactorioExceptionBreakpointAction(), Constraints(Anchor.AFTER, "XDebugger.MuteBreakpoints"))
    }

    private fun debugeeOutput(@Async.Execute dapEvent: DAPEvent) {
        val outputEvent = dapEvent as DAPOutputEvent
        val category = outputEvent.body.category
        if (category == null) {
            logger.warn("Received invalid output event!")
            return
        }
        val contentType = when (category) {
            OutputCategory.IMPORTANT -> ConsoleViewContentType.LOG_ERROR_OUTPUT
            OutputCategory.CONSOLE -> ConsoleViewContentType.LOG_INFO_OUTPUT
            OutputCategory.STDOUT -> ConsoleViewContentType.NORMAL_OUTPUT
            OutputCategory.STDERR -> ConsoleViewContentType.ERROR_OUTPUT
            OutputCategory.TELEMETRY -> ConsoleViewContentType.LOG_DEBUG_OUTPUT
        }
        if ((contentType !== ConsoleViewContentType.NORMAL_OUTPUT || contentType !== ConsoleViewContentType.ERROR_OUTPUT) &&
            !outputEvent.body.output.endsWith("\n")
        ) {
            outputEvent.body.output = outputEvent.body.output + "\n"
        }
        if (ConsoleViewContentType.SYSTEM_OUTPUT !== contentType) myDebugeeConsole.print(
            outputEvent.body.output,
            contentType
        ) else logger.warn("Debugger output: ${category.name} ${outputEvent.body.output}")
    }

    private fun positionReached(stoppedEvent: DAPStoppedEvent, threadInfo: DAPThreadsResponse) {
        CoroutineScope(Job() + Dispatchers.Default).launch {
            try {
                val suspendCtx = createSuspendContext(stoppedEvent, threadInfo)

                var hitBreakpoint: XLineBreakpoint<*>? = null
                val hitRunToCursorBp: Boolean

                val stoppedReason = stoppedEvent.body.reason
                if (stoppedReason === DAPStoppedEvent.SoppedReason.BREAKPOINT ||
                    stoppedReason === DAPStoppedEvent.SoppedReason.FUNCTION_BREAKPOINT ||
                    stoppedReason === DAPStoppedEvent.SoppedReason.DATA_BREAKPOINT ||
                    stoppedReason === DAPStoppedEvent.SoppedReason.INSTRUCTION_BREAKPOINT
                ) {
                    hitRunToCursorBp = stoppedEvent.body.hitBreakpointIds.contains(runToPositionBreakpoint?.first)
                    if (hitRunToCursorBp) {
                        removeBreakpoint(runToPositionBreakpoint!!.second, null)
                        runToPositionBreakpoint = null
                    } else {
                        for (hitBreakpointId in stoppedEvent.body.hitBreakpointIds) {
                            val bp = idToBreakpointMap[hitBreakpointId]
                            if (bp != null) {
                                hitBreakpoint = bp
                                break
                            }
                        }

                        if (hitBreakpoint == null) {
                            // Try to guess which breakpoint has been hit
                            val stacks = suspendCtx.executionStacks
                            val sourcePosition = if (stacks.isNotEmpty()) stacks[0].topFrame?.sourcePosition else null
                            sourcePosition?.let {
                                if (XSourcePosition.isOnTheSameLine(runToPositionBreakpoint?.second, sourcePosition)) {
                                    // We hit a "runToPosition" breakpoint
                                    removeBreakpoint(runToPositionBreakpoint!!.second, null)
                                    runToPositionBreakpoint = null
                                } else {
                                    hitBreakpoint = findBreakpoint(sourcePosition)
                                }
                            }
                        }
                    }
                }

                if (hitBreakpoint != null) {
                    val shouldSuspend = this@FactorioDebugProcess.session.breakpointReached(hitBreakpoint!!, null, suspendCtx)
                    if (!shouldSuspend) resume(suspendCtx)
                } else {
                    val session = this@FactorioDebugProcess.session
                    if (session is XDebugSessionImpl) session.positionReached(suspendCtx, true) else session.positionReached(suspendCtx)
                }
            } catch (err: Throwable) {
                logger.warn("Unable to stop because suspend context creation failed with", err)
                this@FactorioDebugProcess.resume(null)
            }
        }
    }

    private suspend fun createSuspendContext(stoppedEvent: DAPStoppedEvent, threadInfo: DAPThreadsResponse): XSuspendContext {
        return if (stoppedEvent.body.allThreadsStopped != null && stoppedEvent.body.allThreadsStopped!!) {
            FactorioSuspendContext.fromThreads(threadInfo.body.threads, this).await()
        } else {
            val threads = arrayOfNulls<DAPThread>(1)
            for (th in threadInfo.body.threads) {
                if (th.id == stoppedEvent.body.threadId) {
                    threads[0] = th
                    break
                }
            }
            FactorioSuspendContext.fromThreads(threads, this).await()
        }
    }

    private fun findBreakpoint(sourcePosition: XSourcePosition): XLineBreakpoint<*>? {
        for ((_, bp) in idToBreakpointMap) {
            if (bp.fileUrl == sourcePosition.file.url &&
                bp.line == sourcePosition.line
            ) {
                return bp
            }
        }
        return null
    }

    override fun checkCanInitBreakpoints(): Boolean {
        return myDebugger.isInitialized
    }

    override fun sessionInitialized() {
        myDebugger.launch()
    }

    override fun doGetProcessHandler(): ProcessHandler? {
        return this.myProcessHandler
    }

    override fun createConsole(): ExecutionConsole {
        return myDebugeeConsole
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> {
        return myBreakpointHandlers
    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return myEditorsProvider
    }

    override fun getEvaluator(): XDebuggerEvaluator? {
        val frame = this.session.currentStackFrame
        return frame?.evaluator
    }

    override fun startStepOver(context: XSuspendContext?) {
        myDebugger.stepOver()
    }

    override fun startStepOut(context: XSuspendContext?) {
        myDebugger.stepOut()
    }

    override fun startStepInto(context: XSuspendContext?) {
        myDebugger.stepInto(null)
    }

    override fun getSmartStepIntoHandler(): XSmartStepIntoHandler<FactorioSmartStepIntoVariant>? {
        return if (hasCapability(DAPCapabilitiesEnum.StepInTargetsRequest)) {
            mySmartStepIntoHandler
        } else null
    }

    override fun stopAsync(): Promise<Any> {
        if (!hasCapability(DAPCapabilitiesEnum.TerminateRequest)) {
            myDebugger.setTerminating()
            return rejectedPromise()
        }
        return myDebugger.stop().then { r: DAPTerminateResponse -> r }
    }

    override fun startPausing() {
        myDebugger.startPausing()
    }

    override fun resume(context: XSuspendContext?) {
        myDebugger.resume()
    }

    override fun runToPosition(position: XSourcePosition, context: XSuspendContext?) {
        //runToPositionBreakpoint = new Pair<>(-1, position);
        val convertedPosition = myPositionConverter.convertToFactorio(position)
        myDebugger.getBreakpointLocations(convertedPosition.file, convertedPosition.line, -1)
            .onProcessed { bpLocations: DAPBreakpointLocationsResponse? ->
                val bps = bpLocations?.body?.breakpoints
                val line = if (!bps.isNullOrEmpty()) bps[0].line else -1
                if (convertedPosition.line == line) {
                    addInvisibleTemporaryBreakpoint(convertedPosition)
                        .onSuccess { rtpBp: DAPSourceBreakpoint ->
                            val id = rtpBp.additionalProperties["id"]
                            if (id is Int) {
                                runToPositionBreakpoint = Pair(id, position)
                                resume(context)
                            }
                        }
                        .onError { err: Throwable -> this.session.reportError("Unable to run to position: failed to create helper breakpoint: $err") }
                } else {
                    AppUIExecutor.onUiThread().submit {
                        MessagesService.getInstance().showErrorDialog(
                            this.session.project,
                            "No executable code found at\n${position.file.name}:${position.line}",
                            "Run To Cursor"
                        )
                    }
                    //final Collection<NotificationGroup> registeredNotificationGroups = NotificationGroupManager.getInstance().getRegisteredNotificationGroups();
                    //NotificationGroupManager.getInstance().getNotificationGroup("JavaEE execution issue");
                    //this.getSession().reportError("No source code found in line " + position.getLine());
                }
            }
    }

    fun reregisterBreakpoints() {
        (myBreakpointHandlers[0] as FactorioLineBreakpointHandler).reregisterBreakpoints()
    }

    private fun addInvisibleTemporaryBreakpoint(convertedPosition: XSourcePosition): Promise<DAPSourceBreakpoint> {
        // TODO check for acceptable breakpoint locations
        return myDebugger.addBreakpoint(convertedPosition, null)
    }

    fun addBreakpoint(position: XSourcePosition?, breakpoint: XLineBreakpoint<*>) {
        val convertedPosition = myPositionConverter.convertToFactorio(position!!)
        myDebugger.addBreakpoint(convertedPosition, breakpoint)
            .onSuccess { bpResult: DAPSourceBreakpoint? -> handleBreakpointResponse(bpResult, breakpoint) }
            .onError { _: Throwable? -> handleBreakpointResponse(null, breakpoint) }
    }

    fun removeBreakpoint(position: XSourcePosition?, breakpoint: XLineBreakpoint<*>?) {
        val convertedPosition = myPositionConverter.convertToFactorio(position!!)
        myDebugger.removeBreakpoint(convertedPosition, breakpoint)
    }

    private fun handleBreakpointResponse(bpResponse: DAPSourceBreakpoint?, breakpoint: XLineBreakpoint<*>) {
        if (bpResponse != null) {
            val id = bpResponse.additionalProperties["id"]
            if (id is Int) idToBreakpointMap[id] = breakpoint
            if (java.lang.Boolean.TRUE == bpResponse.additionalProperties["verified"]) {
                this@FactorioDebugProcess.session.setBreakpointVerified(breakpoint)
                return
            }
        }
        this@FactorioDebugProcess.session.setBreakpointInvalid(breakpoint, "Unable to verify")
    }

    private fun handleBreakpointEvent(@Async.Execute dapEvent: DAPEvent) {
        val breakpointEvent = dapEvent as DAPBreakpointEvent
        val bp = breakpointEvent.body?.breakpoint
        if (bp?.id != null) {
            val lineBP = idToBreakpointMap[bp.id]
            if (lineBP != null) {
                if (bp.verified) {
                    this.session.setBreakpointVerified(lineBP)
                } else {
                    this.session.setBreakpointInvalid(lineBP, if (bp.message != null) bp.message else "Unable to verify")
                }
            }
        }
    }

    fun getSourcePosition(path: String?, line: Int): FactorioSourcePosition {
        return myPositionConverter.getFactorioSourcePosition(path, line)
    }

    fun getScope(frameId: Int): Promise<DAPScopesResponse> {
        return myDebugger.getScope(frameId)
    }

    fun getVariable(varRev: Int, offset: Int, maxResults: Int): Promise<DAPVariablesResponse> {
        return myDebugger.getVariable(varRev, offset, maxResults)
    }

    fun getStackTrace(id: Int): Promise<DAPStackTraceResponse> {
        return myDebugger.getStackTrace(id)
    }

    fun toJSON(dapMsg: Any?): String {
        return myDebugger.toJSON(dapMsg)
    }

    fun evaluate(
        expression: String,
        frameId: Int,
        context: EvalContext?
    ): Promise<DAPEvaluateResponse> {
        return myDebugger.evaluate(expression, frameId, context)
    }

    fun getCompletions(text: String?, myFrameId: Int, line: Int, column: Int): Promise<DAPCompletionsResponse> {
        return myDebugger.getCompletions(text, myFrameId, line, column)
    }

    fun getStepIntoTargets(frameId: FactorioStackFrame): Promise<DAPStepInTargetsResponse> {
        return myDebugger.getStepIntoTargets(frameId.stackFrameId)
    }

    fun stepInto(variant: FactorioSmartStepIntoVariant) {
        myDebugger.stepInto(variant.targetId)
    }

    fun setValue(myParent: FactorioVariableContainer?, myVariableName: String, expression: String): Promise<DAPVariable?> {
        val res = setExpression(myVariableName, expression, currentFrame)
            .then { evalRes: DAPSetExpressionResponse? -> evalRes?.body?.toVariable() }
        if (res.state == Promise.State.REJECTED && !hasCapability(DAPCapabilitiesEnum.SetVariable)) {
            return rejectedPromise("Operation not supported")
        }
        return if (myParent == null) {
            rejectedPromise("This variable cant be changed because it has no parent!")
        } else myDebugger.setValue(myParent.referenceId, myVariableName, expression)
            .then { setValRes: DAPSetVariableResponse? -> setValRes?.body?.toVariable() }
    }

    private fun setExpression(
        evaluationExpression: String,
        expression: String,
        currentFrame: FactorioStackFrame?
    ): Promise<DAPSetExpressionResponse> {
        return if (!hasCapability(DAPCapabilitiesEnum.SetExpression)) {
            rejectedPromise("Operation not supported")
        } else myDebugger.setExpression(
            evaluationExpression,
            expression,
            currentFrame?.stackFrameId
        )
    }

    private val currentFrame: FactorioStackFrame?
        get() {
            val ctx = this.session.suspendContext
            val stack = ctx?.activeExecutionStack
            val frame = stack?.topFrame
            return if (frame is FactorioStackFrame) frame else null
        }
}
