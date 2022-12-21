package com.satish.fp.tagless.consoleapp

import cats.Id
import cats.effect.{IO, Ref}
import cats.effect.kernel.Sync
import cats.effect.std.Console as CatsConsole

trait Console[F[_]]:
  def readLine: F[String]
  def println(line: String): F[Unit]


object Console:

  def liveConsole : Console[IO] = new LiveConsole[IO]

  class LiveConsole[F[_] : CatsConsole] extends Console[F]:
    def readLine: F[String] = CatsConsole[F].readLine
    def println(line: String): F[Unit] = CatsConsole[F].println(line)

  class FakeConsole[F[_] : Sync](toWrite: Ref[F, List[String]], toRead: Ref[F, List[String]]) extends Console[F]:
    def readLine: F[String] = toRead.modify {
      case Nil => (Nil, "")
      case h :: t => (t, h)
    }
    def println(line: String): F[Unit] = toWrite.update(line :: _)

