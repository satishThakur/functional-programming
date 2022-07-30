package com.satish.fp.application

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*
import cats.effect.{ExitCode, IO, IOApp, Ref}
import com.satish.fp.application.domain.{Goal, User}
import org.http4s.circe.CirceEntityCodec.*
import cats.Monad
import cats.syntax.all.*
import com.satish.fp.application.services.Goals
import org.http4s.server
import com.satish.fp.application.routes.{GoalsRoute, UserRoute}
import com.satish.fp.application.services.Users


object Main extends IOApp:

  def httpApp[F[_] : Monad](goalsRoute : GoalsRoute[F], usersRoute : UserRoute[F]): HttpApp[F] =
    ( goalsRoute.routes <+> usersRoute.routes ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    val goals = Goals.make[IO]
    val goalsRoute = GoalsRoute(goals)

    val sampleUsers = Map(
      "dummy" -> User("dummy", "secret"),
      "admin" -> User("admin", "admin@123"),
    )
    val refUsers = Ref.of[IO,Map[String, User]](sampleUsers)

    (for{
      usersDb <- refUsers
      usersService = Users.testUsers(usersDb)
      usersRoute = UserRoute(usersService)
      apiApp = httpApp(goalsRoute, usersRoute)
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(apiApp)
        .build
        .use(_ => IO.never)
    } yield ()).as(ExitCode.Success)


