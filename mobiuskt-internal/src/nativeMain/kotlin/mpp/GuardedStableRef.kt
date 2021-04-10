package mpp

import kotlinx.cinterop.StableRef
import kotlin.native.concurrent.AtomicReference

class GuardedStableRef<T : Any>(t: T) {
    private var stableRef: StableRef<T> = StableRef.create(t)
    private val threadRef = ThreadRef()
    internal val disposed = AtomicReference(false)

    public val isDisposed
        get() = disposed.value

    val value: T
        get() {
            checkStateAccessValid()
            return stableRef.get()
        }

    fun replace(value: T) {
        checkStateAccessValid()
        stableRef.dispose()
        stableRef = StableRef.create(value)
    }

    fun dispose() {
        checkStateAccessValid()
        stableRef.dispose()
        disposed.value = true
    }

    private fun checkStateAccessValid() {
        check(threadRef.same()) {
            "StableRef can only be accessed from the thread it was created with"
        }

        check(!disposed.value) { "StableRef already disposed" }
    }
}
