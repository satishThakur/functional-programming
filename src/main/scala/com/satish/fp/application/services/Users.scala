package com.satish.fp.application.services
import com.satish.fp.application.domain.User
import cats.effect.{Ref}
import cats.Functor
import cats.syntax.all.*

trait Users[F[_]]:
  def getUser(userId: String): F[Option[User]]
  def createUser(user: User): F[Unit]
  def updatePassword(userId: String, password: String): F[Unit]

object Users:
  def testUsers[F[_] : Functor](ref : Ref[F, Map[String, User]]) : Users[F] =
    new Users[F]:
      override def getUser(userId: String): F[Option[User]] =
        ref.getAndUpdate(identity).map(m => m.get(userId))

      override def createUser(user: User): F[Unit] = ref.update(m => m.updated(user.id, user))

      override def updatePassword(userId: String, password: String): F[Unit] =
        ref.update(m => m.updated(userId, User(userId, password)))

  def testUsers[F[_] : Ref.Make : Functor] : F[Users[F]] =
    val ref : F[Ref[F, Map[String,User]]] = Ref.of[F,Map[String, User]](Map.empty[String, User])
    ref.map(testUsers(_))

