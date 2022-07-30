package com.satish.fp.application.routes.auth

import cats.{Applicative, Monad}
import cats.syntax.all.*
import cats.data.{Kleisli, OptionT}
import org.http4s.*
import org.http4s.headers.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.server.AuthMiddleware
import com.satish.fp.application.domain.User
import com.satish.fp.application.services.Users
import org.http4s.server.middleware.authentication.BasicAuth.authParams

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.Try
import org.typelevel.ci.CIString


case class BasicAuthCreds(username : String, password: String)

object Authenticator:

  //"Basic abcdef"
  def parseCreds(headerValue : String) : Option[BasicAuthCreds] =
    println(s"got header to parse - $headerValue")
    val encodedCreds = headerValue.stripPrefix("Basic").strip()
    val decodedCreads : Option[String] = base64Decode(encodedCreds)
    val creds : Option[(String, String)] = decodedCreads.flatMap {
      dc => dc.split(":").toList match
          case username :: password :: Nil => Some((username, password))
          case _ => None
    }
    creds.map(BasicAuthCreds(_,_))


  def base64Decode(encoded : String): Option[String] =
    val decoded = Try(Base64.getDecoder().decode(encoded)).toOption
    decoded.map(new String(_, StandardCharsets.UTF_8))


object BasicAuth:


  def authMiddleware[F[_]: Monad](userService : Users[F]): AuthMiddleware[F, User] =
    basicAuthMiddleware(realAuthUser(userService))

  def basicAuthMiddleware[F[_]: Monad, U](userAuth: Kleisli[OptionT[F, _], Request[F], U]): AuthMiddleware[F, U] =
    service => Kleisli{
      req =>
        val resp = userAuth(req).value.flatMap{
          case Some(a) => service(AuthedRequest(a, req)).getOrElse(Response[F](Status.NotFound))
          case None => challengeResp.pure[F]
        }
        OptionT.liftF(resp)
    }

  def middleware[F[_] : Monad]: AuthMiddleware[F, User] = AuthMiddleware(authUser)

  def authUser[F[_] : Applicative]: Kleisli[OptionT[F, _], Request[F], User] = Kleisli{
      case GET -> Root / "unauth" / "welcome" => OptionT.liftF(User("dummy", "secret").pure[F])
      case _ => OptionT.none
  }

  def realAuthUser[F[_] : Monad](userService : Users[F]) : Kleisli[OptionT[F, _], Request[F], User] = Kleisli{
    req => {
      println("in realAuthUser...")
      val headerValue = authHeaderValue(req)
      println(s"headerValue - $headerValue")
      val basicCreds = headerValue.flatMap(Authenticator.parseCreds)
      println(s"basiccreds - $basicCreds")
      val liftedCreds : OptionT[F, BasicAuthCreds] = OptionT(basicCreds.pure[F])
      liftedCreds.flatMap(bc => OptionT(userService.getUser(bc.username)))
    }
  }

  def authHeaderValue[F[_]](req : Request[F]) : Option[String] =
    req.headers.get(CIString("Authorization")).map{
      ls => ls.head.value
    }

  def challengeResp[F[_]] : Response[F] =
    val c = Challenge("Basic", "admin", Map("charset" -> "UTF-8"))
    Response(Status.Unauthorized).putHeaders(`WWW-Authenticate`(c))



