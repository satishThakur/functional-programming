package com.satish.fp.application.routes

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import cats.effect.IO
import cats.syntax.all.*
import org.http4s.server.Router

import com.satish.fp.application.routes.auth.BasicAuth
import cats.Monad
import cats.syntax.all.*

import com.satish.fp.application.domain.User
import com.satish.fp.application.services.Users

class UserRoute(userService: Users[IO]):

  val prefix = "rest"

  val route = HttpRoutes.of[IO]{
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }

  val another = AuthedRoutes.of[User, IO]{
    case GET -> Root / "unauth" / "welcome" as user => Ok(s"Welcome, ${user}")
  }

  val auth = AuthedRoutes.of[User, IO]{
    case GET -> Root / "auth" / "welcome" as user => Ok(s"Welcome, ${user.id}")
  }

  val routes = Router(
    prefix -> route,
    prefix -> BasicAuth.authMiddleware[IO](userService)(another),
    prefix -> BasicAuth.authMiddleware[IO](userService)(auth)
  )


