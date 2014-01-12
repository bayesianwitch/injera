package com.bayesianwitch.injera

import scalaz._

package object functions {
  implicit def functionWithPreimageFromTypeclass[K,V](f: K=>V)(implicit hfwp: HasFunctionWithPreimage[K,V]): FunctionWithPreimage[K,V] = new FunctionWithPreimage[K,V] {
    def apply(k: K) = f(k)
    def preimage(v: V) = hfwp.preimageOf(v)
  }
}
