package com.satish.fp.tokenauth

import cats.Monad
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

trait JwtAuthService[F[_]]:
  def createToken(userId: UUID): F[String]
  def validateToken(token: String): F[Option[UUID]]


class JwtAuthServiceImpl[F[_] : JwtClock: Monad : Console] extends JwtAuthService[F]:
  override def createToken(userId: UUID): F[String] =
    val claim = generateClaim(userId)
    claim.map(c => Jwt.encode(c, "secretKey", JwtAlgorithm.HS512))

  override def validateToken(token: String): F[Option[UUID]] =
    val claim: Try[JwtClaim] = Jwt.decode(token, "secretKey", Seq(JwtAlgorithm.HS512))
    claim match
      case Success(value) =>
        val uuid = decode[UUID](value.content.replace('{', '"').replace('}', '"'))
        Console[F].println(value) *>
          Console[F].println(value.content) *>
          Console[F].println(uuid) *>
          Monad[F].pure(Some(uuid.getOrElse(UUID.randomUUID())))
      case _ => Monad[F].pure(None)

  private def generateClaim(userId: UUID): F[JwtClaim] =
    for{
      n <- JwtClock[F].nowEpocSeconds
      _ <- Console[F].println(userId)
      _ <- Console[F].println(userId.asJson.noSpaces)
      exp <- JwtClock[F].futureEpocSeconds(FiniteDuration(1, TimeUnit.DAYS))
    } yield JwtClaim(content = userId.asJson.noSpaces).issuedAt(n).expiresAt(exp)