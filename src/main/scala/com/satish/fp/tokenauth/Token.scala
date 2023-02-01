package com.satish.fp.tokenauth

import cats.MonadThrow
import cats.syntax.all.*
import io.circe.{Decoder, Encoder}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.util.{Success, Failure}

import io.circe.syntax.*
import io.circe.parser.*

/**
 * @author Satish
 * Token generates JWT tokens and verifies them.
 * Tokens store User information which will be used to identify user in stateless manner.
 * @tparam F
 * @tparam User
 */

opaque type SecretKey = String

object SecretKey:
  def apply(key: String): SecretKey = key
  extension (key: SecretKey)
    def value: String = key

case class AsymmetricKeyPair(publicKey: String, privateKey: String)

trait Token[F[_], User]:

    def create(user: User): F[String]

    def verify(token: String): F[User]

object Token:
  def makeSymToken[F[_]: JwtClock: MonadThrow, User: Encoder : Decoder](secret: SecretKey): Token[F, User] =
    new Token[F, User]:
      override def create(user: User): F[String] =
        val claim = generateClaim(user)
        claim.map(c => Jwt.encode(c, secret.value, JwtAlgorithm.HS512))

      override def verify(token: String): F[User] =
        Jwt.decode(token, secret.value, Seq(JwtAlgorithm.HS512)) match
          case Success(value) =>
            val uuid = decode[User](value.content)
            MonadThrow[F].fromEither(uuid.leftMap(_ => ValidationException(ValidationError.InvalidToken)))
          case _ => ValidationException(ValidationError.InvalidToken).raiseError

      private def generateClaim(userClaim: User): F[JwtClaim] =
        for {
          n <- JwtClock[F].nowEpocSeconds
          exp <- JwtClock[F].futureEpocSeconds(FiniteDuration(1, TimeUnit.DAYS))
        } yield JwtClaim(userClaim.asJson.noSpaces).issuedAt(n).expiresAt(exp)

  def makeAsymToken[F[_]: JwtClock: MonadThrow, User: Encoder : Decoder](keyPair: AsymmetricKeyPair): Token[F, User] =
    new Token[F, User]:
      override def create(user: User): F[String] =
        val claim = generateClaim(user)
        claim.map(c => Jwt.encode(c, keyPair.privateKey, JwtAlgorithm.RS256))

      override def verify(token: String): F[User] =
        Jwt.decode(token, keyPair.publicKey, Seq(JwtAlgorithm.RS256)) match
          case Success(value) =>
            val uuid = decode[User](value.content)
            MonadThrow[F].fromEither(uuid.leftMap(_ => ValidationException(ValidationError.InvalidToken)))
          case _ => ValidationException(ValidationError.InvalidToken).raiseError

      private def generateClaim(userClaim: User): F[JwtClaim] =
        for {
          n <- JwtClock[F].nowEpocSeconds
          exp <- JwtClock[F].futureEpocSeconds(FiniteDuration(1, TimeUnit.DAYS))
        } yield JwtClaim(userClaim.asJson.noSpaces).issuedAt(n).expiresAt(exp)
  
end Token