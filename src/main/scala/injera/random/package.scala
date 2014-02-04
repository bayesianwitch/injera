package com.bayesianwitch.injera

import scala.util.Random

package object random {
  implicit class WeightedSeq[T](val seq: Seq[(T,Double)]) extends AnyVal {
    def weightedChoice: T = {
      if (seq.size == 0) {
        throw new IndexOutOfBoundsException("Cannot compute a weighted choice for a sequence of size 0.")
      }
      val totalWeight = seq.map(_._2).sum
      val rnd = Random.nextDouble * totalWeight
      var i: Int = 0
      var cumsum: Double = 0
      var result: Option[T] = None
      while (result.isEmpty) {
        if (seq(i)._2 <= 0) {
          throw new IllegalArgumentException("Cannot have negative weight.")
        }
        cumsum += seq(i)._2
        if ((cumsum >= rnd) || (i == seq.size-1)) {
          result = Some(seq(i)._1)
        }
        i += 1
      }
      result.get
    }
  }
}
