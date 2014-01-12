package com.bayesianwitch.injera.functions

import org.scalacheck._
import Prop._
import scalaz._

object TestPreimage extends Properties("FunctionWithPreimage.laws") {
  object ProjectFirst extends FunctionWithPreimage[(Int,Boolean), Int] {
    def apply(k: (Int,Boolean)) = k._1
    def preimage(v: Int) = NonEmptyList( (v, true), (v, false) )
  }

  property("forward of preimage is correct") = forAll((i: Int, b: Boolean) => {
    val v = ProjectFirst((i,b))
    val p = ProjectFirst.preimage(v)
    p.list.forall( t => ProjectFirst(t) == v)
  })

  property("preimage contains everything") = forAll((i: Int, b: Boolean) => {
    (ProjectFirst((i,b)) == i) ==> ProjectFirst.preimage(i).list.contains((i,b))
  })
}

object TestPreimageTypeclass extends Properties("HasFunctionWithPreimage.laws") {
  trait HasIdents {
    val idents: NonEmptyList[Int]
  }

  case class Identified(idents: NonEmptyList[Int]) extends HasIdents

  implicit object HasIdentsPreimage extends HasFunctionWithPreimage[Int,HasIdents] {
    def preimageOf(v: HasIdents) = v.idents
  }

  def getIdentified(id: Int): Identified = Identified(NonEmptyList(id))

  property("implicit use of HasIdentsPreimage compiles") = forAll((i: Int) => {
    val v = getIdentified(i)
    val p = (getIdentified _).preimage(v)
    p.list.forall( t => getIdentified(t) == v)
  })
}
