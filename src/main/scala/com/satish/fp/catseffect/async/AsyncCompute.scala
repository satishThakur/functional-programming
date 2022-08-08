package com.satish.fp.catseffect.async

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object AsyncCompute extends IOApp :

  def computeExpensive: Future[Int] = Future {
    println(s"In Future ${Thread.currentThread().getName}")
    Thread.sleep(1000)
    100
  }

  def asyncCompute: IO[Int] =
    val c = computeExpensive
    IO.async_(cb => {
      println(s"in callback - ${Thread.currentThread().getName}")
      c.onComplete {
        case Success(value) => println(s"in complete - ${Thread.currentThread().getName}"); cb(Right(value))
        case Failure(exception) => cb(Left(exception))
      }
    })

  override def run(args: List[String]): IO[ExitCode] =
    for {
      v <- asyncCompute
      _ <- IO.println(v)
    } yield ExitCode.Success
