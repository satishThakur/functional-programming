package com.satish.fp.tokenauth

import cats.MonadThrow
import cats.syntax.all.*
import io.circe.{Decoder, Encoder}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}
import io.circe.syntax.*
import io.circe.parser.*

import scala.util.control.NoStackTrace

/**
 * @author Satish
 * Token generates JWT tokens and verifies them.
 * Tokens store User information which will be used to identify user in stateless manner.
 * @tparam F
 * @tparam User
 */

enum ValidationError:
  case InvalidToken extends ValidationError
  case ExpiredToken extends ValidationError

case class ValidationException(reason: ValidationError) extends NoStackTrace

opaque type SecretKey = String

object SecretKey:
  def apply(key: String): SecretKey = key
  extension (key: SecretKey)
    def value: String = key

case class AsymmetricKeyPair(privateKey: String, publicKey: String)

trait Claim[F[_], User]:
  def createClaim(user: User): F[JwtClaim]

object Claim:
  def makeClaim[F[_]: JwtClock: MonadThrow, User: Encoder : Decoder]: Claim[F, User] =
    (user: User) => for {
      n <- JwtClock[F].nowEpocSeconds
      exp <- JwtClock[F].futureEpocSeconds(FiniteDuration(1, TimeUnit.DAYS))
    } yield JwtClaim(user.asJson.noSpaces).issuedAt(n).expiresAt(exp)

trait Token[F[_], User]:

    def create(user: User): F[String]

    def verify(token: String): F[User]

object Token:
  def makeSymToken[F[_]: MonadThrow,User: Encoder : Decoder]
  (claim: Claim[F, User],secret: SecretKey): Token[F, User] =
    new Token[F, User]:
      override def create(user: User): F[String] =
        val c = claim.createClaim(user)
        c.map(c => Jwt.encode(c, secret.value, JwtAlgorithm.HS512))

      override def verify(token: String): F[User] =
        Jwt.decode(token, secret.value, Seq(JwtAlgorithm.HS512)) match
          case Success(value) =>
            val uuid = decode[User](value.content)
            MonadThrow[F].fromEither(uuid.leftMap(_ => ValidationException(ValidationError.InvalidToken)))
          case _ => ValidationException(ValidationError.InvalidToken).raiseError

  def makeAsymToken[F[_]: MonadThrow, User: Encoder : Decoder]
  (claim: Claim[F, User], keyPair: AsymmetricKeyPair): Token[F, User] =
    new Token[F, User]:
      override def create(user: User): F[String] =
        val c = claim.createClaim(user)
        c.map(c => Jwt.encode(c, keyPair.privateKey, JwtAlgorithm.RS256))

      override def verify(token: String): F[User] =
        Jwt.decode(token, keyPair.publicKey, Seq(JwtAlgorithm.RS256)) match
          case Success(value) =>
            val uuid = decode[User](value.content)
            MonadThrow[F].fromEither(uuid.leftMap(_ => ValidationException(ValidationError.InvalidToken)))
          case _ => ValidationException(ValidationError.InvalidToken).raiseError

end Token