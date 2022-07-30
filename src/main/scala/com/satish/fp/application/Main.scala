package com.satish.fp.application

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*
import cats.effect.{ExitCode, IO, IOApp}
import com.satish.fp.application.domain.Goal
import org.http4s.circe.CirceEntityCodec.*
import cats.syntax.all.*
import com.satish.fp.application.services.Goals
import org.http4s.server

import com.satish.fp.application.routes.UserRoute

object Main extends IOApp:

  val goals = Goals.make[IO]

  val goalService = HttpRoutes.of[IO]{
    case GET -> Root / "goals" =>
      Ok(goals.getAll)
  }

  val router = (goalService <+> UserRoute.routes).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(router)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
