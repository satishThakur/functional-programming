package com.satish.fp.cats.error
import cats.{ApplicativeError, ApplicativeThrow, Applicative, Apply, Monoid}
import cats.syntax.all.*
import cats.instances.all.*

object ApplicativeErrors extends App:

  //ApplicativeThrow is ApplicativeError[F, Throwable]

  /**
   * We wants Applicative for operation MapN - but we also want to encode error.
   * So Applicative which can throw error is - ApplicativeError.
   *
   */
  def safeDivision[F[_] : ApplicativeThrow](num: Int, dem: Int): F[Int] =
    if dem == 0 then new ArithmeticException("divide by zero").raiseError
    else (num.pure, dem.pure).mapN(_ / _)


  /**
   *
   * This is hoew typically you would handle error if you want to. Code which does not want
   * to handle error can just work with simple Applicative.
   */
  def handleError[F[_] : ApplicativeThrow, A](v : F[A])(using M : Monoid[A]) : F[A] =
    v.handleErrorWith{
      case _ => M.empty.pure
    }

  val s : Either[Throwable, Int] = safeDivision[[X] =>> Either[Throwable, X]](12,4)
  val p : Either[Throwable, Int] = safeDivision[[X] =>> Either[Throwable, X]](3,0)
  println(s)
  println(p)

  println(handleError(p))

