package com.bayesianwitch.injera.counting

import org.scalacheck._
import Prop._

abstract class TestCounterProperties(name: String) extends Properties(name) {
  def newCounter: Counter[String] with AddableCounter[String] with IterableCounter[String]

  property("incrementing") = forAll((x:String, y: String) => {
    val c = newCounter
    c.inc(x)
    (x != y) ==> ((c.get(x) == 1) && (c.get(y) == 0))
  })
  property("adding") = forAll((x:String, y: String, z: Long) => {
    val c = newCounter
    c.add(x, z)
    (x != y) ==> ((c.get(x) == z) && (c.get(y) == 0))
  })
  property("keys") = forAll((x:String, y: String) => {
    val c = newCounter
    c.inc(x)
    c.inc(y)
    c.keys.toSet == Set(x,y)
  })
}

object TestSimpleCounter extends TestCounterProperties("SimpleCounter") {
  def newCounter = new SimpleCounter[String]

  val a: SimpleCounter[String] = newCounter
  val b: SimpleCounter[String] = newCounter

  import scalaz._
  import Scalaz._

  property("CounterPlus") = forAll((x: String, y: String, z: String) => {
    val c1 = newCounter
    val c2 = newCounter
    c1.inc(x)
    c1.inc(y)
    c2.inc(y)
    c2.inc(z)
    val c = c1 <+> c2
    (Set(x,y,z).size == 3) ==> ((c.get(x) === 1) && (c.get(y) === 2) && (c.get(z) === 1))
  })
}
