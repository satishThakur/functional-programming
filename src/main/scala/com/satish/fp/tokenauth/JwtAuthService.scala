package com.satish.fp.tokenauth

import cats.{Monad, MonadError, MonadThrow}
import cats.effect.std.Console
import cats.syntax.all.*

import java.util.UUID
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.time.Clock
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.{Success, Try}
import io.circe.syntax.*
import io.circe.parser.*

import scala.util.control.NoStackTrace

enum ValidationError:
  case InvalidToken extends ValidationError
  case ExpiredToken extends ValidationError

case class ValidationException(reason: ValidationError) extends NoStackTrace

trait JwtAuthService[F[_]]:
  def createToken(userClaim: UserClaim): F[String]
  def validateToken(token: String): F[UserClaim]

class JwtAuthServiceImpl[F[_] : JwtClock: MonadThrow : Console] extends JwtAuthService[F]:
  override def createToken(userClaim: UserClaim): F[String] =
    val claim = generateClaim(userClaim)
    claim.map(c => Jwt.encode(c, "secretKey", JwtAlgorithm.HS512))

  override def validateToken(token: String): F[UserClaim] =
    Jwt.decode(token, "secretKey", Seq(JwtAlgorithm.HS512)) match
      case Success(value) =>
        val uuid = decode[UserClaim](value.content)
        MonadThrow[F].fromEither(uuid.leftMap(_ => ValidationException(ValidationError.InvalidToken)))
      case _ => ValidationException(ValidationError.InvalidToken).raiseError

  private def generateClaim(userClaim: UserClaim): F[JwtClaim] =
    for{
      n <- JwtClock[F].nowEpocSeconds
      exp <- JwtClock[F].futureEpocSeconds(FiniteDuration(1, TimeUnit.DAYS))
    } yield JwtClaim(userClaim.asJson.noSpaces).issuedAt(n).expiresAt(exp)