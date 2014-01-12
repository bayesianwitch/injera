package com.bayesianwitch.injera.deduplication

import org.scalacheck._
import Prop.forAll

object TestDeduplicator extends Properties("Deduplicator.equality") {
  property("structural equality") = forAll((x:String) => {
    val dedup = new Deduplicator[String]()
    (x == dedup(x)) && (x eq dedup(x))
  })
}
