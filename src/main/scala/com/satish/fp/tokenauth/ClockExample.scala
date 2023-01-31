package com.satish.fp.tokenauth

import cats.Functor
import cats.syntax.all.*
import cats.effect.{IO, IOApp}
import cats.effect.kernel.Clock

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object ClockExample:

  def someFunc[F[_] : Functor: Clock]: F[FiniteDuration] =
    //Clock[F].realTimeInstant
    Clock[F].realTime.map(_ + FiniteDuration(1, TimeUnit.HOURS))

object ClockMain extends IOApp.Simple:
  import ClockExample.*
  override def run: IO[Unit] = someFunc[IO].flatMap(i => IO.println(i))