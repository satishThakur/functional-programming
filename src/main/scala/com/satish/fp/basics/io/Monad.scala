package com.satish.fp.basics.io

trait Functor[F[_]]:
  extension[A](fa: F[A])
    def map[B](f : A => B) : F[B]



trait Monad[F[_]] extends Functor[F]:

  def unit[A](a : => A): F[A]

  extension[A](fa: F[A])
    def flatMap[B](f : A => F[B]) : F[B]

    def map[B](f : A => B) : F[B] =
      flatMap(a => unit(f(a)))

    def map2[B,C](fb : F[B])(f : (A,B) => C) : F[C] =
      flatMap( a => fb.map(b => f(a,b)))

    //product
    def **[B](fb : F[B]) : F[(A,B)] = map2(fb)((_,_))

    //evaluate both effects but discard left one
    def *>[B](fb : F[B]) : F[B] = flatMap(_ => fb)

    //evaluate both but return left -discard right
    def <*[B](fb : F[B]) : F[A] = fb.flatMap(_ => fa)

    //we can not just do unit(b) - as that would not evaluate the current effect fa.
    //we still want to evaluate fa but irrespective of result just lift b to effect.
    def as[B](b : B) : F[B] = map(_ => b)

    def forever: F[A] = flatMap(_ => forever)

object Monad:
  given f0moand : Monad[Function0] with
    override def unit[A](a: => A): () => A = () => a
    extension[A](fa : Function0[A])
      def flatMap[B](f: A => Function0[B]) : Function0[B] =
        () => f(fa())()



