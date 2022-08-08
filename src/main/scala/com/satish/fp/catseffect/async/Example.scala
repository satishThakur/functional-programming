package com.satish.fp.catseffect.async

import cats.effect.{ExitCode, IO, IOApp}
import java.util.concurrent.Executors

object Computation:

  def computeSomething (oncompletion : Int => Unit): Unit =
    val ec = Executors.newSingleThreadExecutor()

    ec.submit( new Runnable {
      override def run(): Unit =
        println(s"doing massive computations on ${Thread.currentThread().getName}")
        oncompletion(34)
    })


  def computeAsync : IO[Int] = IO.async_(cb => {
    println(s"callback on ${Thread.currentThread().getName}")
    computeSomething( i => {println(s"data arrived  on ${Thread.currentThread().getName}"); cb(Right(i))})
  })

object Example extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    for{
      _ <- Computation.computeAsync
      _ <- IO.println(s"Further computations on ${Thread.currentThread().getName}")
    } yield ExitCode.Success


