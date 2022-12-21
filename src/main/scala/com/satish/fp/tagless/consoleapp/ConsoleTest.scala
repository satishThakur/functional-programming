package com.satish.fp.tagless.consoleapp

import cats.effect.{IO, IOApp, Ref}
import com.satish.fp.tagless.consoleapp.Console.FakeConsole

object ConsoleTest extends IOApp.Simple:

  override def run: IO[Unit] =
    for{
      reader <- Ref.of[IO, List[String]](List("satish"))
      writer <- Ref.of[IO, List[String]](List.empty)
      fakeConsole = new FakeConsole(writer,reader)
      _ <- Application.someFunc(fakeConsole)
      l <- writer.get
      _ <- IO.println(l)
      r <- reader.get
      _ <- IO.println(r)
    }yield()
