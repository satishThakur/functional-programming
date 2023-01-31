package com.satish.fp.tokenauth

import cats.effect.{IO, IOApp}

import java.util.UUID

object AuthApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val service = new JwtAuthServiceImpl[IO]
    val userId = UUID.randomUUID()
    println(userId)
    for{
      token <- service.createToken(UserClaim(userId))
      _ <- IO.println(token)
      id <- service.validateToken(token)
      _ <- IO.println(id)
    }yield ()

