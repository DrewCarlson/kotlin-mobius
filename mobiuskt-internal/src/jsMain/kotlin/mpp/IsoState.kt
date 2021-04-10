package mpp

actual class BackgroundStateRunner : StateRunner {
    actual override fun <R> stateRun(block: () -> R): R = block()
    actual override fun stop() {
    }
}

actual class StateHolder<T : Any> internal actual constructor(t: T, actual val stateRunner: StateRunner) {

    private var _state: T = t
    actual val myState: T get() = _state

    private var _isDisposed: Boolean = false

    actual val isDisposed: Boolean
        get() = _isDisposed

    actual fun replaceState(newState: T) {
        _state = newState
    }

    actual fun dispose() {
        if (!isDisposed) _isDisposed = true
    }

    actual val myThread: Boolean = true
}

internal actual val defaultStateRunner: StateRunner = BackgroundStateRunner()
