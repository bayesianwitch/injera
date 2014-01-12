package com.bayesianwitch.injera.misc

object ConsumeParams {
  def apply[K,V,T](p: Map[K,V])(f: Params[K,V] => T): T = {
    val params = new ParamsImpl[K,V](p)
    val result = f(params)
    result
  }

  def consume[K,V](k: K)(implicit params: Params[K,V]) = params match {
    case (p:ParamsImpl[K,V]) => {
      val v = p.result(k)
      p.result = p.result - k
      v
    }
  }

  def maybeConsume[K,V](k: K)(implicit params: Params[K,V]) = params match {
    case (p:ParamsImpl[K,V]) => {
      val v = p.result.get(k)
      if (v.isDefined) {
        p.result = p.result - k
      }
      v
    }
  }

  def remainder[K,V](implicit params: Params[K,V]) = params match {
    case (p:ParamsImpl[K,V]) => p.result
  }

  sealed trait Params[K,V]

  private class ParamsImpl[K,V](p: Map[K,V]) extends Params[K,V] {
    var result = p
  }
}
