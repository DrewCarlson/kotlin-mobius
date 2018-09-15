package com.spotify.mobius.runners

import com.spotify.mobius.functions.Producer


/**
 * Provides the default Event and Effect work runners used by [Mobius.loop] when
 * configuring a default [MobiusLoop.Builder].
 */
expect class DefaultWorkRunners() {
  fun eventWorkRunnerProducer(): Producer<WorkRunner>
  fun effectWorkRunnerProducer(): Producer<WorkRunner>
}
