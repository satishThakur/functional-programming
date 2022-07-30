package com.satish.fp.application.routes

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import cats.effect.IO
import com.satish.fp.application.domain.Goal
import org.http4s.circe.CirceEntityCodec.*
import cats.Monad
import cats.syntax.all.*
import com.satish.fp.application.services.Goals
import org.http4s.dsl.Http4sDsl
import org.http4s.server
import org.http4s.server.Router

class GoalsRoute[F[_]: Monad] (goalsService : Goals[F]) extends Http4sDsl[F]:

  private val prefix = "rest"

  private val goalsRoute : HttpRoutes[F] = HttpRoutes.of[F]{
    case GET -> Root / "goals" => Ok(goalsService.getAll)
  }

  val routes: HttpRoutes[F] = Router(
    prefix -> goalsRoute
  )



