# Injera

A utility library used by BayesianWitch.

## Installation

Add this to your build.scala file:

    resolvers ++= "chrisstucchio" at "http://maven.chrisstucchio.com/",
    libraryDependencies ++= "com.chrisstucchio" %% "injera" % "0.01"

# Utilities

## com.bayesianwitch.injera.deduplication

The class `Deduplicator[T]` is provided. This class is mathematically the identity function:

    val deduplicator = new Deduplicator[T](maximumSize = 1024, ttlInSeconds = 60)
    x == deduplicator(x)

The purpose of the deduplicator is solely to reduce memory usage - if `x == y` then `deduplicator(x)` and `deduplicator(y)` will share the same memory location. The [rationale can be found here](http://www.chrisstucchio.com/blog/2013/deduplication.html). It takes `maximumSize` and `ttlInSeconds` arguments.
