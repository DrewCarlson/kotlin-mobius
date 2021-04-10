package kt.mobius

import kt.mobius.runners.Runnable
import kt.mobius.runners.WorkRunner
import mpp.IsolateState

class MobiusLoopController<M, E, F>(
    private val loopFactory: MobiusLoop.Factory<M, E, F>,
    private val defaultModel: M,
    private val mainThreadRunner: WorkRunner
) : MobiusLoop.Controller<M, E>, ControllerActions<M, E> {
    private object LOCK

    private val stateContainer = IsolateState<ControllerStateBase<M, E>> {
        ControllerStateInit(this, defaultModel)
    }

    override val isRunning: Boolean
        get() = mpp.synchronized(LOCK) {
            stateContainer.access { it.isRunning }
        }

    override val model: M
        get() = mpp.synchronized(LOCK) {
            stateContainer.access { it.onGetModel() }
        }

    private fun dispatchEvent(event: E) {
        stateContainer.access { it.onDispatchEvent(event) }
    }

    private fun updateView(model: M) {
        stateContainer.access { it.onUpdateView(model) }
    }

    override fun connect(view: Connectable<M, E>): Unit = mpp.synchronized(LOCK) {
        stateContainer.access { it.onConnect(view) }
    }

    override fun disconnect(): Unit = mpp.synchronized(LOCK) {
        stateContainer.access { it.onDisconnect() }
    }

    override fun start(): Unit = mpp.synchronized(LOCK) {
        stateContainer.access { it.onStart() }
    }

    override fun stop(): Unit = mpp.synchronized(LOCK) {
        stateContainer.access { it.onStop() }
    }

    override fun replaceModel(model: M): Unit = mpp.synchronized(LOCK) {
        stateContainer.access { it.onReplaceModel(model) }
    }

    override fun postUpdateView(model: M) {
        mainThreadRunner.post(
            object : Runnable {
                override fun run() {
                    updateView(model)
                }
            })
    }

    override fun goToStateInit(nextModelToStartFrom: M): Unit = mpp.synchronized(LOCK) {
        stateContainer.replace { ControllerStateInit(this, nextModelToStartFrom) }
    }

    override fun goToStateCreated(renderer: Connection<M>, nextModelToStartFrom: M?): Unit =
        mpp.synchronized(LOCK) {
            val safeRenderer = object : Connection<M> {
                override fun accept(value: M) {
                    mainThreadRunner.post(
                        object : Runnable {
                            override fun run() {
                                renderer.accept(value)
                            }
                        }
                    )
                }

                override fun dispose() {

                }
            }
            val nextModel = nextModelToStartFrom ?: defaultModel
            stateContainer.replace { ControllerStateCreated<M, E, F>(this, renderer, nextModel) }
        }

    override fun goToStateCreated(view: Connectable<M, E>, nextModelToStartFrom: M) {

        val safeModelHandler = SafeConnectable(view)

        val modelConnection = safeModelHandler.connect { event -> dispatchEvent(event) }

        goToStateCreated(modelConnection, nextModelToStartFrom)
    }

    override fun goToStateRunning(renderer: Connection<M>, nextModelToStartFrom: M): Unit =
        mpp.synchronized(LOCK) {
            val stateRunning = ControllerStateRunning(this, renderer, loopFactory, nextModelToStartFrom)

            stateContainer.replace { stateRunning }
            stateContainer.access { start() }
        }
}
