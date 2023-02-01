package com.satish.fp.tokenauth
import JwtService.*

import cats.effect.{IO, IOApp}

object AsymAuthApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val service = new JwtServiceImpl[IO]
    for{
      token <- service.encodeAsymetric()
      _ <- IO.println(token)
      claim <- service.decodeAsymetric(token)
      _ <- IO.println(claim)
    } yield ()

