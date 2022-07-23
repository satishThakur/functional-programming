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
import com.satish.fp.application.program.Goals

object Main extends IOApp:

  val goals = Goals.make[IO]

  val helloService = HttpRoutes.of[IO]{
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }

  val goalService = HttpRoutes.of[IO]{
    case GET -> Root / "goals" =>
      Ok(goals.getAll)
  }

  val router = (helloService <+> goalService).orNotFound


  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(router)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
