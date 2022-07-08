package com.satish.fp.cats.monad.transformer

object MonadTx:
  import cats.data.OptionT
  import cats.Applicative.*
  import cats.syntax.applicative.*
  import cats.data.Kleisli

  type EitherOrError[A] = Either[Error, A]
  case class User(id: Int, username: String, password: String)
  case class Address(raw: String)

  type optionUserOrError[A] = OptionT[EitherOrError, A]
  def lookupUser(id: Int): OptionT[EitherOrError, User] =
    User(123, "dave", "secret").pure[optionUserOrError]


  //if we have to map - we need to do nested mapping.
  //def userName(id: Int): Either[Error, Option[String]] = lookupUser(id).map(u => u.map(_.username))
  def lookupUserAddress(user: User): OptionT[EitherOrError,Address] =
    Address("abc city - pin code - 123456").pure[optionUserOrError]

  def getAddress(userId: Int): OptionT[EitherOrError, Address] = for {
    u <- lookupUser(userId)
    add <- lookupUserAddress(u)
  } yield add

object MonadExMain extends App:
  import MonadTx.*

  println(getAddress(123).value)

