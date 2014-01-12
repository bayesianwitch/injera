# Injera

A utility library used by BayesianWitch.

## Installation

Add this to your build.scala file:

    resolvers ++= "chrisstucchio" at "http://maven.chrisstucchio.com/",
    libraryDependencies ++= "com.chrisstucchio" %% "injera" % "0.01"

# Utilities

## com.bayesianwitch.injera.functions

The trait `FunctionWithPreimage[K,V]` represents a function for which the preimage can be computed:

    trait FunctionWithPreimage[K,V] extends (K => V) {
      def preimage(v: V): NonEmptyList[K]
    }

For any `FunctionWithPreimage[K,V]` It must satisfy the following two laws:

    val p = f.preimage(v)
    p.forAll( x => f(x) == v)

and

    (f(k) == v) ==> f.preimage(v).contains(k)

There is also a typeclass `HasFunctionWithPreimage[K,V]` which asserts that *any* function of type `K => V` has a single preimage.

One use case of this typeclass is inverting and caching database lookups. For example:

    trait HasPhoneNumbers {
      def phoneNumbers: NonEmptyList[PhoneNumber]
    }

You might have multiple functions looking people up based on their phone number:

    class Person(...) extends HasPhoneNumbers
    def getPersonByPhoneNumber(p: PhoneNumber): Person

    class Business(...) extends HasPhoneNumbers
    def getBusinessByPhoneNumber(p: PhoneNumber): Business

Provided `getPersonByPhoneNumber` and `getBusinessByPhoneNumber` are implemented in the obvious way, then the following typeclass would be useful:

    implicit object HasFunctionWithPreimage[PhoneNumber,HasPhoneNumbers] {
      def preimageOf(v: V) = v.phoneNumbers
    }

## com.bayesianwitch.injera.deduplication

The class `Deduplicator[T]` is provided. This class is mathematically the identity function:

    val deduplicator = new Deduplicator[T](maximumSize = 1024, ttlInSeconds = 60)
    x == deduplicator(x)

The purpose of the deduplicator is solely to reduce memory usage - if `x == y` then `deduplicator(x)` and `deduplicator(y)` will share the same memory location. The [rationale can be found here](http://www.chrisstucchio.com/blog/2013/deduplication.html). It takes `maximumSize` and `ttlInSeconds` arguments.
