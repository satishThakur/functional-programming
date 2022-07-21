package com.satish.fp.application

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp:
  val helloRoute = HttpRoutes.of[IO]{
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")

  }.orNotFound
  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(helloRoute)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
