### Introduction
HTTP4S is a Scala library which implements Typeful and functional HTTP server and client. 
HTTP4S is built on top of Cats Effect and Cats. It is a pure functional library which is type safe and composable.
HTTP4S uses FS2 for streaming and Cats effect for concurrency. This document would outline the high level 
design of HTTP4S and how it can be used to build a functional HTTP server.

### Core Constructs
A simple HTTP server can be thought of following function:
```scala
Request => Response
```
But we would need to perform some side effects to get the response. For example, we would need to query a database to get the response. Hence:
```scala
Request => F[Response]
```
Where `F` is the effect type. We would use `F[_]` to denote the effect type.

Not every possible route would have a response and hence our function would return an `Option[Response]`:
```scala
Request => F[Option[Response]]
```
We would use `OptionT[F, Response]` to denote this.
```scala
Request => OptionT[F, Response]
```
If we model the above function as a Kleisli, we would get:
```scala
Kleisli[OptionT[F, _], Request, Response]
```
What we defined above is called Service in HTTP4S. We can compose services using Kleisli composition. 
A bit of modification to the types would lead us to:
```scala
Kleisli[OptionT[F, _], Request[F], Response[F]]
```
Which is a core construct in HTTP4S aliased `HttpRoutes[F]`.