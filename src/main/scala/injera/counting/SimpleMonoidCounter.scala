package com.bayesianwitch.injera.counting

import scala.collection.mutable.HashMap
import scalaz._
import Scalaz._

class SimpleMonoidCounter[T,V](implicit val monoid: Monoid[V]) extends MonoidCounter[T,V] with MonoidIterableCounter[T,V] with MonoidZeroableCounter[T,V] {
  //NOT THREAD SAFE
  private var counts = new HashMap[T,V]()

  def keys = counts.keys.iterator

  def add(t: T, v: V): V = {
    val newCount = get(t) |+| v
    counts.put(t, newCount)
    newCount
  }

  def get(t: T): V = counts.get(t).getOrElse(monoid.zero)

  def zero: Map[T,V] = {
    val result = counts.toMap
    counts = new HashMap[T,V]()
    result
  }


}
