package com.satish.fp.tokenauth
import java.util.UUID
import cats.effect.{IO, IOApp}

object TokenApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val token = Token.makeSymToken[IO,UserClaim](SecretKey("superSecret"))

    for{
      t <- token.create(UserClaim(UUID.randomUUID()))
      _ <- IO.println(t)
    } yield ()
