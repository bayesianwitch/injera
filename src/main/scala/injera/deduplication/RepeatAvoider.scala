package com.bayesianwitch.injera.deduplication

import com.google.common.cache._
import java.util.concurrent.TimeUnit

class RepeatAvoider[T <: Object](maximumSize: Long = 1024, expireTime: Int = 10) {
  /* This is like a deduplicator, but is meant to be used with functions having side effects.

   For example, inserting a value in an idempotent manner into a database.
   */
  private val cache: Cache[T,T] = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireTime, TimeUnit.MINUTES).build();

  def apply(k: T)(f: T => Unit) = {
    if (cache.getIfPresent(k) == null) {
      f(k)
      cache.put(k,k)
    }
  }

  def avoidRepeatsLater(k: T)(f: T => Unit): () => Unit = {
    /* This us used when you don't necessarily want to prevent repeats immediately. For example:

     val rp = new RepeatAvoider()
     val noRepeat = rp.avoidRepeatsLater("foo")(key => insertIntoDatabaseNoCommit(key, databaseConnection) )
     ...
     databaseConnection.commit()
     noRepeat()
     */
    if (cache.getIfPresent(k) == null) {
      f(k)
      () => cache.put(k,k)
    } else {
      () => ()
    }
  }
}
