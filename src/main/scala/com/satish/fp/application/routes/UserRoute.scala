package com.satish.fp.application.routes

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import cats.effect.IO
import cats.syntax.all.*
import org.http4s.server.Router
import com.satish.fp.application.routes.middleware.BasicAuth
import cats.Monad
import cats.syntax.all.*
import com.satish.fp.application.domain.User
import com.satish.fp.application.services.Users
import org.http4s.dsl.Http4sDsl

class UserRoute[F[_] : Monad](userService: Users[F]) extends Http4sDsl[F]:

  private val prefix = "rest"

  private val route : HttpRoutes[F] = HttpRoutes.of[F]{
    case GET -> Root / name =>
      Ok(s"Hello, $name.")
  }

  val another : AuthedRoutes[User, F] = AuthedRoutes.of[User, F]{
    case GET -> Root / "welcome" as user => Ok(s"Welcome, ${user}")
  }

  val auth : AuthedRoutes[User, F] = AuthedRoutes.of[User, F]{
    case GET -> Root / "auth" / "welcome" as user => Ok(s"Welcome, ${user.id}")
  }

  val routes: HttpRoutes[F] = Router(
    prefix + "/hello" -> route,
    prefix + "/unauth" -> BasicAuth.middleware[F](userService)(another),
    prefix -> BasicAuth.middleware[F](userService)(auth)
  )


