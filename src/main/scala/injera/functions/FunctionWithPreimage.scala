package com.bayesianwitch.injera.functions

import scalaz._
import Scalaz._

trait ComputesPreimage[K,-V] {
  def preimage(v: V): NonEmptyList[K]
  def partialInverse(v: V): K = preimage(v).head
}

trait FunctionWithPreimage[K,V] extends (K => V) with ComputesPreimage[K,V]

trait FunctionMWithPreimage[K,V,M[_]] extends (K => M[V]) with ComputesPreimage[K,V] {
  protected implicit val mApplicative: Applicative[M]
  def preimageM(v: V): M[NonEmptyList[K]] = v.point[M].map(preimage)
}

trait HasFunctionWithPreimage[K,-V] {
  def preimageOf(v: V): NonEmptyList[K]
}
