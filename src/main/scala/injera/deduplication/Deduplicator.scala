package com.bayesianwitch.injera.deduplication

import com.google.common.cache._
import java.util.concurrent.TimeUnit

class Deduplicator[T <: Object](maximumSize: Long = 1024, ttlInSeconds: Int = 60*10) {
  private val cache: Cache[T,T] = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(ttlInSeconds, TimeUnit.SECONDS).build()

  def apply(t: T): T = {
    val result = cache.getIfPresent(t)
    if (result != null) {
      result
    } else {
      cache.put(t, t)
      t
    }
  }
}
