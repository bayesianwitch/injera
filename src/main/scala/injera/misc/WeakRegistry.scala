package com.bayesianwitch.injera.misc

import java.util.{UUID, WeakHashMap}
import java.lang.ref.{WeakReference, SoftReference}
import scala.collection.mutable.{HashMap => MHashMap}

trait Registry[K,V] {
  def get(key: K): Option[V]
  def register(value: V): K
  protected def generateKey(value: V): K //does not have to be deterministic
}

trait GeneratableKeyWeakReferenceRegistry[K,V] extends Registry[K,V] {
  private val valueToKeyMap = new WeakHashMap[V,K]
  protected val keyToValueMap: MHashMap[K,SoftReference[V]] = new MHashMap[K,SoftReference[V]]()

  def register(value: V): K = {
    val fromValue = valueToKeyMap.get(value)
    if (fromValue != null) {
      fromValue
    } else this.synchronized {
      val key = generateKey(value)
      valueToKeyMap.put(value, key)
      keyToValueMap += (key -> new SoftReference(value))
      key
    }
  }

  def get(k: K): Option[V] = keyToValueMap.get(k).flatMap( v => {
      val derefed = v.get
      if (derefed == null) {
        keyToValueMap -= k
        None
      } else {
        Some(derefed)
      }
    })
}

trait FlushesKeyToValueMap[K,V] {
  protected def keyToValueMap: MHashMap[K,SoftReference[V]]
  def flushKeyToValueMap: Unit = this.synchronized {
    val nullKeys = keyToValueMap.filter( kv => kv._2.get == null)
    nullKeys.foreach( kv => keyToValueMap -= kv._1 )
  }
}

class UUIDWeakReferenceRegistry[V] extends GeneratableKeyWeakReferenceRegistry[UUID,V] with FlushesKeyToValueMap[UUID,V] {
  protected def generateKey(value: V) = UUID.randomUUID
}
