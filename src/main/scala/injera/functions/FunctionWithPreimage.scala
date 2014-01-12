package com.bayesianwitch.injera.functions

import scalaz._

trait FunctionWithPreimage[K,V] extends (K => V) {
  def preimage(v: V): NonEmptyList[K]
  def partialInverse(v: V): K = preimage(v).head
}

trait HasFunctionWithPreimage[K,-V] {
  def preimageOf(v: V): NonEmptyList[K]
}
