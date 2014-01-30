package com.bayesianwitch.injera.counting

import scalaz._
import Scalaz._

trait CounterUtils {

  implicit def counterPlus[V[T] <: IterableCounter[T] with AddableCounter[T]](implicit factory: IterableAddableCounterFactory[V]): Plus[V] = new Plus[V] {
    //I'm not entirely sure I like this, probably should use CBF pattern?
    def plus[A](a: V[A], b: =>V[A]): V[A] = {
      val result = factory.newZero(a)
      val bb = b
      a.keys.foreach(k => result.add(k, a.get(k)))
      bb.keys.foreach(k => result.add(k, bb.get(k)))
      result
    }
  }
}
