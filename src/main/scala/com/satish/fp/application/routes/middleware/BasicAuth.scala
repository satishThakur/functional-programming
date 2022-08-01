package com.satish.fp.application.routes.middleware

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

/**
 * Represent Credentials from Basic Authentication sent by the client.
 * @param username - Username sent by the client
 * @param password - Password sent by the client
 */
case class BasicAuthCreds(username : String, password: String)

object BasicAuthCreds:

  /**
   * Apply takes the header value from basic authentication and converts it to BasicAuthCreds. As the
   * conversion can fail - it may or may not be able to return - hence Optional
   * @param headerValue - header value for basic auth.
   * @return - Optional BasicAuthCreds after parsing the value.
   */
  def apply(headerValue : String) : Option[BasicAuthCreds] =
    val encodedCreds: String = headerValue.stripPrefix("Basic").strip()
    for{
      decodedCreds <- base64Decode(encodedCreds)
      basicCreds <- convertToCres(decodedCreds)
    }yield basicCreds

  /**
   * converts the decoded creds to BasicAuthCreds. We get decoded creads in the form of username:password.
   * @param usernamePass - string which has the form of username:password
   * @return if parsing success then BasicAuthCreds else None.
   */
  private def convertToCres(usernamePass: String): Option[BasicAuthCreds] =
    usernamePass.split(":").toList match
      case username :: password :: Nil => Some(BasicAuthCreds(username, password))
      case _ => None

  private def base64Decode(encoded : String): Option[String] =
    val decoded = Try(Base64.getDecoder().decode(encoded)).toOption
    decoded.map(new String(_, StandardCharsets.UTF_8))


object BasicAuth:

  def middleware[F[_]: Monad](userService : Users[F]): AuthMiddleware[F, User] =
    middleware(authUser(userService))

  def middleware[F[_]: Monad, U](userAuth: Kleisli[OptionT[F, _], Request[F], U]): AuthMiddleware[F, U] =
    service => Kleisli{
      req =>
        OptionT(userAuth(req).value.flatMap{
          case Some(a) => service(AuthedRequest(a, req)).value
          case None => Some(challengeResp).pure[F]
        })
    }

  def authUser[F[_] : Monad](userService : Users[F]) : Kleisli[OptionT[F, _], Request[F], User] = Kleisli{
    req => {
      val headerValue: Option[String] = authHeaderValue(req)
      val basicCreds: Option[BasicAuthCreds] = headerValue.flatMap(BasicAuthCreds(_))
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



