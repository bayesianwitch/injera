package com.bayesianwitch.injera.functions

import org.scalacheck._
import Prop._
import scalaz._

object TestPreimageCaching extends Properties("CacheFunctionWithPreimage") {
  class ProjectFirst extends FunctionWithPreimage[(Int,Boolean), Int] {
    var timesCalled: Int = 0
    def apply(k: (Int,Boolean)) = {
      timesCalled += 1
      k._1
    }
    def preimage(v: Int) = NonEmptyList( (v, true), (v, false) )
  }

  class ProjectFirstCache(val fwpi: ProjectFirst) extends CacheFunctionWithPreimage[(Int,Boolean), Int] {
    private val cache = new scala.collection.mutable.HashMap[(Int,Boolean),Int]()

    protected def putImpl(k: (Int,Boolean), v: Int) = cache += (k -> v)
    protected def invalidateImpl(k: (Int,Boolean)) = cache -= k
    protected def getFromCache(k: (Int,Boolean)) = cache.get(k)
  }

  property("forward of preimage is correct") = forAll((i: Int) => {
    val pf = new ProjectFirst
    val pfc = new ProjectFirstCache(pf)

    val v = pfc.get((i,true))
    val v2 = pfc.get((i,false))
    (v == v2) && (pf.timesCalled == 1)
  })
}
