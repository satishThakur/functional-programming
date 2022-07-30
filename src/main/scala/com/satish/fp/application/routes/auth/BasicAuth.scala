package com.satish.fp.application.routes.auth

import cats.{Monad, Applicative}
import cats.syntax.all.*
import cats.data.{Kleisli, OptionT}
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.server.AuthMiddleware
import com.satish.fp.application.domain.User



object BasicAuth:

  def middleware[F[_] : Monad]: AuthMiddleware[F, User] = AuthMiddleware(authUser)

  def authUser[F[_] : Applicative]: Kleisli[OptionT[F, _], Request[F], User] = Kleisli{
      case GET -> Root / "unauth" / "welcome" => OptionT.liftF(User("dummy", "secret").pure[F])
      case _ => OptionT.none
  }

