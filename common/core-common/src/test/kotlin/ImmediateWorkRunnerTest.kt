import com.spotify.mobius.runners.ImmediateWorkRunner
import com.spotify.mobius.runners.Runnable
import kotlin.test.BeforeTest
import kotlin.test.Test

class ImmediateWorkRunnerTest {

  lateinit var underTest: ImmediateWorkRunner

  @BeforeTest
  fun setUp() {
    underTest = ImmediateWorkRunner()
  }

  @Test
  fun shouldNotRunAfterDispose() {
    underTest.dispose()
    underTest.post(
        object : Runnable {
          override fun run() {
            throw AssertionError("should not execute runnables after dispose")
          }
        })
  }
}
