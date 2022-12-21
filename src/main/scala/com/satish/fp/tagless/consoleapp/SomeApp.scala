package com.satish.fp.tagless.consoleapp
import cats.Monad
import cats.syntax.all.*
import cats.effect.{IO, IOApp}
import com.satish.fp.tagless.consoleapp.Console.liveConsole


object SomeApp extends IOApp.Simple {
  def run: IO[Unit] =  Application.someFunc[IO](liveConsole)
}

object Application:
  def someFunc[F[_] : Monad](console: Console[F]): F[Unit] =
    for {
      _ <- console.println("enter your name")
      name <- console.readLine
      _ <- console.println(s"Hello $name")
    }yield ()

