package com.bayesianwitch.injera.functions

import scalaz._
import Scalaz._

trait WritableStore[-K,-V] {
  protected def putImpl(k: K, v: V): Unit
  def put(k: K, v: V): Unit = putImpl(k,v)
  protected def invalidateImpl(k: K): Unit
  def invalidate(k: K): Unit = invalidate(k)
}

trait WritesEntirePreimage[K,V] extends WritableStore[K,V] {
  protected implicit val cpi: ComputesPreimage[K,V]
  override def put(k: K, v: V) = cpi.preimage(v).foreach(kp => putImpl(kp, v))
  def putObj(v: V) = cpi.preimage(v).foreach(kp => putImpl(kp, v))
  def invalidateObj(v: V): Unit = cpi.preimage(v).foreach(kp => invalidateImpl(kp))
}

trait ReadableStore[-K,+V] {
  def get(k: K): V
}

trait CacheFunctionWithPreimage[K,V] extends ReadableStore[K,V] with WritesEntirePreimage[K,V] {
  protected def getFromCache(k: K): Option[V]
  protected implicit val cpi = fwpi
  protected implicit val fwpi: FunctionWithPreimage[K,V]

  def get(k: K): V = getFromCache(k).getOrElse({
    val result = fwpi(k)
    putObj(result)
    result
  })

  override def invalidate(k: K): Unit = {
    getFromCache(k).foreach(invalidateObj)
    invalidateImpl(k)
  }
}

trait FunctorReadableStore[-K,V, M[_]] {
  protected implicit val mFunctor: Applicative[M]
  def get(k: K): M[V]
}

trait FunctorCacheFunctionWithPreimage[K,V,M[_]] extends FunctorReadableStore[K,V,M] with WritesEntirePreimage[K,V] {
  protected implicit val mPlus: Plus[M]

  protected implicit val cpi = fwpi
  protected implicit val fwpi: FunctionMWithPreimage[K,V,M]

  protected def getFromCache(k: K): M[V]

  def get(k: K): M[V] = mPlus.plus(getFromCache(k), {
    val result = fwpi(k)
    result.map( v => putObj(v))
    result
  })
}
