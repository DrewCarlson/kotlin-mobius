package mpp


expect class StateHolder<T : Any> internal constructor(t: T, stateRunner: StateRunner) {
    val isDisposed: Boolean
    val myThread: Boolean
    val stateRunner: StateRunner
    val myState: T
    fun replaceState(newState: T)
    fun dispose()
}

class IsolateState<T : Any>(private val stateHolder: StateHolder<T>) {
    constructor(producer: () -> T) : this(createState(producer))

    val isDisposed: Boolean
        get() = stateHolder.isDisposed

    fun <R : Any> fork(r: R): StateHolder<R> = if (stateHolder.myThread) {
        StateHolder(r, stateHolder.stateRunner)
    } else {
        error("Must fork state from the state thread")
    }

    fun <R> access(block: (T) -> R): R {
        return if (stateHolder.myThread) {
            block(stateHolder.myState)
        } else {
            stateHolder.stateRunner.stateRun {
                block(stateHolder.myState)
            }
        }
    }

    fun replace(block: (T) -> T) {
        if (stateHolder.myThread) {
            stateHolder.replaceState(block(stateHolder.myState))
        } else {
            stateHolder.stateRunner.stateRun {
                stateHolder.replaceState(block(stateHolder.myState))
            }
        }
    }

    fun dispose() = if (stateHolder.myThread) {
        stateHolder.dispose()
    } else {
        stateHolder.stateRunner.stateRun {
            stateHolder.dispose()
        }
    }
}

interface StateRunner {
    fun <R> stateRun(block: () -> R): R
    fun stop()
}

expect class BackgroundStateRunner : StateRunner {
    override fun <R> stateRun(block: () -> R): R
    override fun stop()
}

internal expect val defaultStateRunner: StateRunner

fun <T : Any> createState(producer: () -> T, stateRunner: StateRunner = defaultStateRunner): StateHolder<T> =
    stateRunner.stateRun { StateHolder(producer(), stateRunner) }

/**
 * Hook to shutdown iso-state default runtime
 */
fun shutdownIsoRunner() {
    defaultStateRunner.stop()
}

internal sealed class RunResult
internal data class Ok<T>(val result: T) : RunResult()
internal data class Thrown(val throwable: Throwable) : RunResult()

