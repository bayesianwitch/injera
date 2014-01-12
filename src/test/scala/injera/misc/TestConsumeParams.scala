package com.bayesianwitch.injera.misc

import org.scalacheck._
import Prop._

object TestConsumeParams extends Properties("ConsumeParams") {
  import ConsumeParams._

  property("parameters consumed, remainder returned") = forAll((x:String, m: Map[String,String]) => {
    val mNoX = m - x
    val result = ConsumeParams(mNoX + (x -> x))(implicit p => {
      (consume(x), remainder)
    })
    result == (x, mNoX)
  })
  property("parameters maybe consumed equality") = forAll((x:String, m: Map[String,String]) => {
    val mNoX = m - x
    val result = ConsumeParams(mNoX)(implicit p => {
      maybeConsume(x)
    })
    result == None
  })
  property("parameters missing after consumed") = forAll((x:String, m: Map[String,String]) => {
    val mNoX = m - x
    val result = ConsumeParams(mNoX + (x -> x))(implicit p => {
      (consume(x), maybeConsume(x))
    })
    result == (x, None)
  })
  property("exception thrown when argument missing") = forAll((x:String, m: Map[String,String]) => {
    val mNoX = m - x

    { ConsumeParams(mNoX)(implicit p => { consume(x) }) } throws classOf[java.util.NoSuchElementException]
  })
}
