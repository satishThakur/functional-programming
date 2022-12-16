## Kleisli
Kleisli enables us to do composition over functions which return monadic values. For example:
Lets say we have:
```scala
f : A => Option[B]
g : B => Option[C]
```
These functions can not be composed as the result of f does not match the input to g. If we compare the same to normal functions like:

```scala
f : Int => String
g : String => List[String]
```
These functions compose and we can define `h = f compose g`. 

Kleisli simply is a wrapper around `A => F[B]` defined as `Kleisli[F, A, B]`. 
What can we do with Kleisli depends upon properties of `F`. For example:
* If we have implicit `Monad[F]` in scope then we can do operations like `flatMap`, `compose`.
* If we have implicit `Functor[F]` in scope we can map over Kleisli. 

### TypeClass Instances
Similarly depending upon `F` we would automatically have typeclass instances for Kleisli. For example if we have `Monad[F]` then we would have `Monad[Kleisli[F[_]]]` available. 
Please note that as for any other function here we would keep both `F` and input `A` constant and let the output be free. For exmaple:
`type MyKleisli[F[_], A] = Kleisli[F,A,_]`

### Monad Transformer
As we know that Monad Transformers help us composing nested Monads. For example `EitherT[F, E, A]` would let us compose `F[Either]` for any `F` which is a Monad. 
Similarly Kleisli can be through of Monad Transformers for functions. As we are abstracting over `A => F[B]`, it let us compose over these functions without wrapping and unwrapping these funcions. 

### Reader Monad
One such Monad Transformer could be where the effect does nothing. So a `type Reader[A,B] = Kleisli[ID, A, B] where type ID[A] = A`.
