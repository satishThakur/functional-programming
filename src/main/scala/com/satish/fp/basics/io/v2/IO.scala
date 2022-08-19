package com.satish.fp.basics.io.v2

import com.satish.fp.basics.io.Monad
import scala.io.StdIn.readLine
import scala.annotation.tailrec

enum IO[A]:
  case  Return(a : A)
  case Suspend(resume : () => A)
  case FlatMap[A,B](sub : IO[A], k : A => IO[B]) extends IO[B]

  def flatMap[B](f : A => IO[B]) : IO[B] = FlatMap(this, f)

  def map[B](f : A => B): IO[B] = flatMap(a => Return(f(a)))

  @tailrec
  final def unsafeRun(): A = this match
    case Return(a) => a
    case Suspend(r) => r()
    case FlatMap(x, f) => x match
      case Return(a) => f(a).unsafeRun()
      case Suspend(r) => f(r()).unsafeRun()
      case FlatMap(y, g) => y.flatMap(a => g(a).flatMap(f)).unsafeRun()

object IO:
  given ioMoand : Monad[IO] with
    override def unit[A](a: => A): IO[A] = Suspend(() => a)
    extension[A](fa: IO[A])
      def flatMap[B](f : A => IO[B]) : IO[B] = fa.flatMap(f)

  def apply[A](a : => A): IO[A] = Suspend(() => a)

object IOMainApp extends App:
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

