package com.bayesianwitch.injera.counting

import scalaz._

trait MonoidCounter[T,V] {
  val monoid: Monoid[V]
  //mutable counters
  def add(t: T, v: V): V
  def get(t: T): V
}

trait MonoidIterableCounter[T,V] extends MonoidCounter[T,V] {
  def keys: Iterator[T]
}

trait MonoidZeroableCounter[T,V] extends MonoidCounter[T,V] {
  //The counter can be zeroed out, returning the old counts
  def zero: Map[T,V]
}
