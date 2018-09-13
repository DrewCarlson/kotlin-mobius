package com.spotify.mobius

import com.spotify.mobius.functions.Consumer


/**
 * This class represents the result of calling an [Update] function.
 *
 *
 * Upon calling an Update function with an Event and Model, a Next object will be returned that
 * contains the new Model (if there is one) and Effect objects that describe which side-effects
 * should take place.
 */
class Next<M, F> private constructor(
    /** Get the model of this Next, if it has one. Might return null. */
    private val model: M?,
    /** Get the effects of this Next. Will return an empty set if there are no effects */
    private val effects: Set<F>
) {

  fun model() = model
  fun effects() = effects

  /** Check if this Next contains a model.  */
  fun hasModel() = model != null

  /** Check if this Next contains effects.  */
  fun hasEffects() = !effects.isEmpty()

  /**
   * Try to get the model from this Next, with a fallback if there isn't one.
   *
   * @param fallbackModel the default model to use if the Next doesn't have a model
   */
  fun modelOrElse(fallbackModel: M): M {
    return if (hasModel()) {
      modelUnsafe()
    } else {
      fallbackModel
    }
  }

  /**
   * Get the model of this Next. This version is unsafe - if this next doesn't have a model, calling
   * this method will cause an exception to be thrown.
   *
   *
   * In almost all cases you should use [.modelOrElse] or [.ifHasModel] instead.
   *
   * @throws NoSuchElementException if this Next has no model
   */
  fun modelUnsafe(): M {
    if (!hasModel()) {
      throw NoSuchElementException("there is no model in this Next<>")
    }
    return model!!
  }

  /** If the model is present, call the given consumer with it, otherwise do nothing.  */
  fun ifHasModel(consumer: Consumer<M>) {
    if (hasModel()) {
      consumer.accept(modelUnsafe())
    }
  }

  override fun toString(): String {
    return "Next(model=$model, effects=$effects)"
  }

  override fun equals(other: Any?): Boolean {
    if (other == null || other !is Next<*, *>) return false
    if (other.model != model) return false
    if (!other.effects.containsAll(effects)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = model?.hashCode() ?: 0
    result = 31 * result + effects.hashCode()
    return result
  }

  companion object {
    /** Create a Next that updates the model and dispatches the optional set of effects. */
    fun <M, F> next(model: M, effects: Set<F> = emptySet()): Next<M, F> {
      return Next(model, effects.toSet())
    }

    /** Create a Next that doesn't update the model but dispatches the supplied effects. */
    fun <M, F> dispatch(effects: Set<F>): Next<M, F> {
      return Next(null, effects.toSet())
    }

    /** Create an empty Next that doesn't update the model or dispatch effects. */
    fun <M, F> noChange(): Next<M, F> {
      return Next(null, emptySet())
    }
  }
}
