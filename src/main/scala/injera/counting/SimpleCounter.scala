package com.bayesianwitch.injera.counting

import scala.collection.mutable.HashMap

object SimpleCounter extends IterableAddableCounterFactory[SimpleCounter] {
  def apply[T]() = new SimpleCounter[T]
  def newZero[T](old: SimpleCounter[T]) = new SimpleCounter[T]
}

class SimpleCounter[T] extends ZeroableCounter[T] with IterableCounter[T] with AddableCounter[T] {
  //NOT THREAD SAFE
  private var counts = new HashMap[T,Long]()

  def keys = counts.keys.iterator

  def inc(t: T): Long = add(t,1L)

  def add(t: T, n: Long): Long = {
    val newCount = n + get(t)
    counts.put(t, newCount)
    newCount
  }

  def get(t: T): Long = counts.get(t).getOrElse(0L)

  def zero: Map[T,Long] = {
    val result = counts.toMap
    counts = new HashMap[T,Long]()
    result
  }
}
