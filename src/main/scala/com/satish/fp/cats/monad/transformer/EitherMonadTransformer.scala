package com.satish.fp.cats.monad.transformer

import cats.Monad
import cats.syntax.applicative.*
import cats.syntax.flatMap.*

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

/**
 *
 * This demonstrates how Monad Transformer can be implemented once the inner monad is concrete.
 * This examples lets us create Any Monad of shape F[Either[E,A]] - Where F is any Monad.
 */
case class MyEitherT[F[_] : Monad, E, A](value : F[Either[E,A]]):
  def unit[A](a: A) : MyEitherT[F,E,A] = MyEitherT(Right(a).pure[F])
  def flatMap[B](f: A => MyEitherT[F,E,B]) : MyEitherT[F,E,B] =
    MyEitherT(value.flatMap{
      case Left(e) => Left(e).pure[F]
      case Right(a) => f(a).value
    })

  def map[B](f : A => B) : MyEitherT[F,E,B] =
    flatMap[B](a => unit(f(a)))

def x : Future[Either[String,Int]] = Future(Right(10))

def y : Future[Either[String,Int]] = Future(Right(20))

def z : Future[Either[String,Int]] = Future(Left("error"))



object EitherMonadTransformer extends App:

  val sum: Future[Either[String, Int]] = (for{
    xv <- MyEitherT(x)
    yb <- MyEitherT(y)
    zv <- MyEitherT(z)
  }yield xv + yb + zv).value

  println(Await.result(sum, 2.second))
