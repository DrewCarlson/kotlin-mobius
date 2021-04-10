package mpp

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual class BackgroundStateRunner : StateRunner {
    internal val stateWorker = Worker.start(errorReporting = false)

    actual override fun <R> stateRun(block: () -> R): R {
        val result = stateWorker.execute(
            TransferMode.SAFE, { block.freeze() },
            {
                try {
                    Ok(it()).freeze()
                } catch (e: Throwable) {
                    Thrown(e).freeze()
                }
            }
        ).result
        @Suppress("UNCHECKED_CAST")
        return when (result) {
            is Ok<*> -> result.result as R
            is Thrown -> throw result.throwable
        }
    }

    actual override fun stop() {
        stateWorker.requestTermination()
    }
}


/**
 * Do not directly use this. You will have state issues. You can only interact with this class
 * from the state thread.
 */
actual class StateHolder<T : Any> internal actual constructor(t: T, actual val stateRunner: StateRunner) {
    private val stableRef: GuardedStableRef<T>

    init {
        check(!t.isFrozen) { "Mutable state shouldn't be frozen" }
        t.ensureNeverFrozen()
        stableRef = GuardedStableRef(t)
    }

    actual val myState: T
        get() = stableRef.value

    actual fun replaceState(newState: T) {
        stableRef.replace(newState)
    }

    actual fun dispose() {
        stableRef.dispose()
    }

    private val threadRef = ThreadRef()
    actual val myThread: Boolean
        get() = threadRef.same()

    actual val isDisposed: Boolean
        get() = stableRef.isDisposed
}

@SharedImmutable
internal actual val defaultStateRunner: StateRunner = BackgroundStateRunner()
