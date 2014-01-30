package com.bayesianwitch.injera.deduplication

import scala.collection.mutable.HashMap
import com.google.common.cache._
import java.util.concurrent.TimeUnit

class SimpleRepeatAvoider[T <: Object] extends RepeatAvoider[T] {
  //This will likely overload your memory
  private val cache = new HashMap[T,T]()
  protected def check(k: T) = cache.get(k).isDefined
  protected def set(k: T) = cache += (k -> k)
}

class TimeSpaceLimitedRepeatAvoider[T <: Object](maximumSize: Long = 1024, expireTime: Int = 10) extends RepeatAvoider[T] {
  private val cache: Cache[T,T] = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireTime, TimeUnit.MINUTES).build();
  protected def check(k: T) = (cache.getIfPresent(k) == null)
  protected def set(k: T) = cache.put(k,k)
}
