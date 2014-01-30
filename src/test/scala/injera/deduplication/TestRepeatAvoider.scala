package com.bayesianwitch.injera.deduplication

import org.scalacheck._
import Prop._

abstract class TestRepeatAvoider(name: String) extends Properties(name) {
  def newRepeatAvoider: RepeatAvoider[String]

  property("avoid repeats") = forAll((x: String) => {
    val r = newRepeatAvoider
    var count: Int = 0
    r(x)(s => { count += 1 })
    r(x)(s => { count += 1 })
    count == 1
  })

  property("but don't avoid non-repeats") = forAll((x: String, y: String) => {
    val r = newRepeatAvoider
    var count: Int = 0
    r(x)(s => { count += 1 })
    r(y)(s => { count += 1 })
    count == Set(x,y).size
  })

  property("delayed repeat avoidance") = forAll((x: String) => {
    val r = newRepeatAvoider
    var count: Int = 0
    val f = r.avoidRepeatsLater(x)(s => { count += 1 })
    r.avoidRepeatsLater(x)(s => { count += 1 })
    f()
    r.avoidRepeatsLater(x)(s => { count += 1 })
    count == 2
  })
}

object TestSimpleRepeatAvoider extends TestRepeatAvoider("RepeatAvoiders.SimpleRepeatAvoider") {
  def newRepeatAvoider = new SimpleRepeatAvoider[String]
}

object TestTimeSpaceLimitedRepeatAvoider extends TestRepeatAvoider("RepeatAvoiders.TimeSpaceLimitedRepeatAvoider") {
  def newRepeatAvoider = new TimeSpaceLimitedRepeatAvoider[String]()
}

object TestBloomFilterRepeatAvoider extends TestRepeatAvoider("RepeatAvoiders.BloomFilterRepeatAvoider") {
  import com.google.common.hash.{BloomFilter,Funnel, PrimitiveSink}
  def newRepeatAvoider = new BloomFilterRepeatAvoider[String]()(
    new Funnel[String]() {
      def funnel(t:String, sink: PrimitiveSink) = {sink.putBytes(t.getBytes())}
    }
  )

}
