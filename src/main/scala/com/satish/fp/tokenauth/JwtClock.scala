package com.satish.fp.tokenauth

import cats.effect.kernel.Clock
import cats.Functor
import cats.syntax.functor.*
import scala.concurrent.duration.FiniteDuration

trait JwtClock[F[_]]:

  def futureEpocSeconds(duration: FiniteDuration): F[Long]
  def nowEpocSeconds: F[Long]

object JwtClock:

  def apply[F[_]](using clock: JwtClock[F]): JwtClock[F] = clock

  given systemClock[F[_]: Clock: Functor]: JwtClock[F] with

    def futureEpocSeconds(duration: FiniteDuration): F[Long] =
      Clock[F].realTime.map(_ + duration).map(_.toSeconds)

    def nowEpocSeconds: F[Long] = Clock[F].realTime.map(_.toSeconds)