package com.satish.fp.basics.io.v1

import com.satish.fp.basics.io.Monad

import scala.io.StdIn.readLine

sealed trait IO[A]:
  self =>
  def unsafeRun() : A

  def map[B](f : A => B) : IO[B] = new IO[B]:
    override def unsafeRun(): B = f(self.unsafeRun())

  def flatMap[B](f: A => IO[B]) : IO[B] = new IO[B]:
    override def unsafeRun(): B = f(self.unsafeRun()).unsafeRun()


object IO:
  def unit[A](a : => A): IO[A] = new IO[A]:
    override def unsafeRun(): A = a

  def apply[A](a : => A) : IO[A] = unit(a)


  given ioMonad : Monad[IO] with
    override def unit[A](a: => A): IO[A] = IO.unit(a)
    extension[A](fa : IO[A])
      def flatMap[B](f : A => IO[B]) : IO[B] = fa.flatMap(f)


object IOApp extends App:

  val input : IO[String] = IO{readLine}

  def output(str : String) : IO[Unit] = IO{println(str)}

  val singleEcho : IO[Unit] = for{
    _ <- output("Enter some text")
    text <- input
    _ <- output(text.toUpperCase)
  }yield ()

  val echoLoop : IO[Unit] = singleEcho.forever

  //echoLoop.unsafeRun()

  IO{println("hello..")}.forever.unsafeRun()

