package com.satish.fp.cats.monad.transformer
import cats.effect.{ExitCode, IO, IOApp}
import cats.{Monad}
import cats.data.EitherT
import cats.syntax.flatMap.*
import cats.syntax.applicative.*
import cats.syntax.functor.*
import scala.util.control.NoStackTrace

sealed trait AuthError extends NoStackTrace
case object UserNotExists extends AuthError
case object NoPassMatch extends AuthError


case class User(id: String, email: String)

trait UserAuth[F[_]]:
  def userExists(userId: String): F[Either[AuthError, User]]

  def passwordMatch(user: User, password: String): F[Either[AuthError, Unit]]

  def subscribed(user: User) : F[Either[AuthError, Unit]]

  def isActive(user: User): F[Either[AuthError, Unit]]

/**
 *
 * Without Monad transformer we have unwrap the inner monad, wrap it back to outer in case of errors and do multiple maps etc.
 * This makes code hard to read and maintain.
 */
def authUser[F[_]: Monad](users: UserAuth[F], userId: String, passworsd: String): F[Either[AuthError, User]] =
  users.userExists(userId).flatMap {
    case e@Left(_) => e.pure[F]
    case Right(user) => users.passwordMatch(user, passworsd).flatMap {
      case Left(e) => Left(e).pure[F]
      case Right(_) => users.subscribed(user).flatMap {
        case Left(e) => Left(e).pure[F]
        case Right(_) => users.isActive(user).map(e => e.map(_ => user))
      }
    }
  }

/**
 *
 * The same functionality using EitherT becomes easy to implement.
 */
def authUserV2[F[_]: Monad](users: UserAuth[F], userId: String, passworsd: String): F[Either[AuthError, User]] =
  (for{
    user <- EitherT(users.userExists(userId))
    _ <- EitherT(users.passwordMatch(user, passworsd))
    _ <- EitherT(users.subscribed(user))
    _ <- EitherT(users.isActive(user))
  }yield user).value


object UserAuthApp extends IOApp:
  override def run(args: List[String]): IO[ExitCode] = ???

