# Injera

A utility library used by BayesianWitch.

## Installation

Add this to your build.scala file:

    resolvers ++= "chrisstucchio" at "http://maven.chrisstucchio.com/",
    libraryDependencies ++= "com.chrisstucchio" %% "injera" % "0.01"

# Utilities

## com.bayesianwitch.injera.functions

### FunctionWithPreimage

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

#### Caching

An important use case for the `FunctionWithPreimage` trait is pre-warming a cache. Suppose you wish to implement a cache. You might write it as follows:

    class RedisCache(...) = {
      def put(k: K, v: V) = shoveIntoRedis(serialize(k), serialize(v)v)
      def get(k: K): V = pullOutOfRedis(serialize(k))
      def invalidate(k: K) = deleteFromRedis(serialize(k))
    }

However, the problem is that the following will result in two cache misses:

    businessByPhoneNumberCache.get(PhoneNumber(111-111-1111))
    //returns Business(PhoneNumber(111-111-1111), PhoneNumber(222-222-2222))

    businessByPhoneNumberCache.get(PhoneNumber(222-222-2222))
    //cache miss!

Simply by mixing in the property traits, we can build a cache which does not have this issue. Here is how:

    class RedisCache extends CacheFunctionWithPreimage[PhoneNumber,Business] {
      protected implicit val fwpi = getBusinessByPhoneNumber

      protected def putImpl(k: PhoneNumber, v: Business) = ...put into redis...
      protected def invalidateImpl(k: PhoneNumber) = ...delete from redis...
      protected def getFromCache(k: PhoneNumber) = ...pull from redis...
    }

The new `RedisCache` will now populate all keys on insert, not simply the one that an object was accessed with.

There is also a functor-ized version for which `getFromCache` returns a result wrapped inside an `ApplicativePlus[_]` instance. This is useful for, e.g., [spray](http://spray.io) style caching, which returns a `Future[V]` rather than a `V`.

## com.bayesianwitch.injera.deduplication

The class `Deduplicator[T]` is provided. This class is mathematically the identity function:

    val deduplicator = new Deduplicator[T](maximumSize = 1024, ttlInSeconds = 60)
    x == deduplicator(x)

The purpose of the deduplicator is solely to reduce memory usage - if `x == y` then `deduplicator(x)` and `deduplicator(y)` will share the same memory location. The [rationale can be found here](http://www.chrisstucchio.com/blog/2013/deduplication.html). It takes `maximumSize` and `ttlInSeconds` arguments.
