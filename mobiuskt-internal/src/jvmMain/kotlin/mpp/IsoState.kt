package mpp

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

actual class StateHolder<T : Any> internal actual constructor(t: T, actual val stateRunner: StateRunner) {

    private var _state: T = t
    actual val myState: T get() = _state

    private var _isDisposed: AtomicBoolean = AtomicBoolean(false)

    actual val isDisposed: Boolean
        get() = _isDisposed.get()

    actual fun replaceState(newState: T) {
        _state = newState
    }

    actual fun dispose() {
        _isDisposed.set(true)
    }

    private val threadRef = ThreadRef()
    actual val myThread: Boolean
        get() = threadRef.same()
}

internal actual val defaultStateRunner: StateRunner = BackgroundStateRunner()


actual class BackgroundStateRunner : StateRunner {
    internal val stateExecutor = Executors.newSingleThreadExecutor()

    actual override fun <R> stateRun(block: () -> R): R {
        val result = stateExecutor.submit(
            Callable<RunResult> {
                try {
                    Ok(block())
                } catch (e: Throwable) {
                    Thrown(e)
                }
            }
        ).get()

        return when (result) {
            is Ok<*> -> result.result as R
            is Thrown -> throw result.throwable
        }
    }

    actual override fun stop() {
        stateExecutor.shutdown()
    }
}
