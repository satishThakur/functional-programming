### Introduction
HTTP4S is a Scala library which implements Typeful and functional HTTP server and client. 
HTTP4S is built on top of Cats Effect and Cats. It is a pure functional library which is type safe and composable.
HTTP4S uses FS2 for streaming and Cats effect for concurrency. This document would outline the high level 
design of HTTP4S and how it can be used to build a functional HTTP server.

### Core Constructs - HttpRoutes and HttpApp
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

#### HttpApp
HttpApp is a type alias for `Kleisli[F[_], Request[F], Response[F]]` - which is a function that takes a request and returns a response effectfully.
This differs from `HttpRoutes` in that it is a total function, and therefore has no way to represent an empty response.

#### AuthedRoutes
AuthedRoutes is a type alias for `Kleisli[OptionT[F, _], AuthedRequest[F, T], Response[F]]` - which is a function that takes a request and returns a response effectfully.
Here `AuthedRequest[F, T]` is a wrapper around `Request[F]` that adds an `AuthedRequest#context` of type `T`. This is useful for adding authentication information to the request.
Think of it as a `Request` with some additional context. In most of the cases this additional context would be authentication information like user id, email, etc.

#### Middlewares
Middlewares are used to transform the request and response. For example, we can add a middleware to log the request and response.
We can think of a middleware as a function that takes a service and returns a service. 
```scala
HttpRoutes[F] => HttpRoutes[F]
```
This translates to a Kleisli function:
```scala
Kleisli[OptionT[F, _], Request[F], Response[F]] => Kleisli[OptionT[F, _], Request[F], Response[F]]
```
The above is called `HttpMiddleware[F]`.
This is how we would typically define a middleware:
```scala
def myMiddleware[F[_]](http: HttpRoutes[F]): HttpRoutes[F] = Kleisli { req =>
  // do something with the request
  http(req).map { resp =>
    // do something with the response
    resp
  }
}
val myService: HttpRoutes[F] = myMiddleware()
```
#### AuthMiddleware
AuthMiddleware is used for authentication. This middleware is responsible for extracting the authentication information from the request and adding it to the context of the request. 
The type of `AuthMiddleware` is:
```scala
Kleisli[OptionT[F,_], AuthedRequest[F, T], Response[F]] => Kleisli[OptionT[F,_], Request[F], Response[F]]
```
Where `T` is the type of the context. This is how we would typically define an auth middleware:
```scala
def myAuthMiddleware[F[_], T](http: AuthedRoutes[T, F]): HttpRoutes[F] = service => Kleisli { req =>
  // do something with the request
  //find the context
  val context = ???
  http(AuthedRequest(req, context)).map { resp =>
    // do something with the response
    resp
  }
}
