package com.bayesianwitch.injera.misc

import org.scalacheck._
import java.util.UUID
import Prop._

object TestWeakRegistry extends Properties("WeakRegistry") {

  property("objects garbage collected from weak registry") = forAll((x:String) => {
    val r = new UUIDWeakReferenceRegistry[UUID]
    val k = r.register(UUID.randomUUID) //Make sure to dynamically generate object for test
    try {
      new Array[Int](1024*1024*1024) //Throw an out of memory error to force a GC
    } catch {
      case (x:OutOfMemoryError) => ()
    }
    System.gc
    r.get(k) == None
  })
  property("non-gced objects stay in registry") = forAll((x:String) => {
    val r = new UUIDWeakReferenceRegistry[UUID]
    val v = UUID.randomUUID
    val k = r.register(v)
    r.get(k) == Some(v)
  })
}
