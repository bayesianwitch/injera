package com.bayesianwitch.injera.counting

import org.scalacheck._
import Prop._

abstract class TestCounterProperties(name: String) extends Properties(name) {
  def newCounter: Counter[String] with AddableCounter[String] with IterableCounter[String]

  property("incrementing") = forAll((x:String, y: String) => {
    val c = new SimpleCounter[String]
    c.inc(x)
    (x != y) ==> ((c.get(x) == 1) && (c.get(y) == 0))
  })
  property("adding") = forAll((x:String, y: String, z: Long) => {
    val c = new SimpleCounter[String]
    c.add(x, z)
    (x != y) ==> ((c.get(x) == z) && (c.get(y) == 0))
  })
  property("keys") = forAll((x:String, y: String) => {
    val c = new SimpleCounter[String]
    c.inc(x)
    c.inc(y)
    c.keys.toSet == Set(x,y)
  })
}

object TestSimpleCounter extends TestCounterProperties("SimpleCounter") {
  def newCounter = new SimpleCounter[String]
}
