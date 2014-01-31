package com.bayesianwitch.injera.deduplication

trait RepeatAvoider[T <: Object] {
  /* This is like a deduplicator, but is meant to be used with functions having side effects.

   For example, inserting a value in an idempotent manner into a database.

   To implement, the check and set functions must be defined. Semantics:

   val k = ...
   check(k) === true
   set(k)
   check(k) === false

   In most practical applications this relationship will be only approximate.
   */
  protected def check(k: T): Boolean
  protected def set(k: T): Unit

  def apply(k: T)(f: => Unit): Unit = {
    if (check(k)) {
      f
      set(k)
    }
  }
  def avoidRepeatsLater(k: T)(f: => Unit): () => Unit = {
    /* This us used when you don't necessarily want to prevent repeats immediately. For example:

     val rp = new RepeatAvoider()
     val noRepeat = rp.avoidRepeatsLater("foo")(key => insertIntoDatabaseNoCommit(key, databaseConnection) )
     ...
     databaseConnection.commit()
     noRepeat()
     */
    if (check(k)) {
      f
      () => set(k)
    } else {
      () => ()
    }
  }
}
