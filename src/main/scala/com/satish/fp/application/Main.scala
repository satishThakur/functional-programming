package com.satish.fp.application

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.ember.server.*
import com.comcast.ip4s.*
import cats.effect.{ExitCode, IO, IOApp, Ref}
import com.satish.fp.application.domain.Goal
import org.http4s.circe.CirceEntityCodec.*
import cats.syntax.all.*
import com.satish.fp.application.services.Goals
import org.http4s.server

import com.satish.fp.application.routes.UserRoute
import com.satish.fp.application.domain.User
import com.satish.fp.application.services.Users
import com.satish.fp.application.routes.UserRoute

object Main extends IOApp:

  def routes: IO[HttpApp[IO]] =
    val goals = Goals.make[IO]
    val goalService = HttpRoutes.of[IO]{
      case GET -> Root / "goals" =>
        Ok(goals.getAll)
    }
    val sampleUsers = Map(
      "dummy" -> User("dummy", "secret"),
      "admin" -> User("admin", "admin@123"),
    )

    val refUsers = Ref.of[IO,Map[String, User]](sampleUsers)

    for{
      userMap <- refUsers
      users = Users.testUsers[IO](userMap)
      userRoutes = new UserRoute(users)

    }yield (goalService <+> userRoutes.routes).orNotFound


  override def run(args: List[String]): IO[ExitCode] =
    (for{
      router <- routes
      _ <- EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(router)
        .build
        .use(_ => IO.never)
    } yield ()).as(ExitCode.Success)


