package com.satish.fp.cats.monad.transformer

object WrappedMonads extends App:

  case class User(id: Int, username: String, password: String)

  case class Address(raw: String)

  case class Demand(dem : String)

  def lookupUser(id: Int): Either[Error, Option[User]] =
    Right(Some(User(123, "Jerry", "secret")))

  //if we have to map - we need to do nested mapping.
  def userName(id: Int): Either[Error, Option[String]] = lookupUser(id).map(u => u.map(_.username))


  def lookupUserAddress(user: User): Either[Error, Option[Address]] =
    Right(Some(Address("abc city - pin code - 123456")))

  def demandOnAddress(address: Address) : Either[Error, Option[Demand]] = Right(Some(Demand("high")))



  //Nesting flatmap - where we have to unwrap and then wrap the inner Monad
  def getDemand(userId: Int): Either[Error, Option[Demand]] =
    lookupUser(userId).flatMap{
      case None => Left(Error("***"))
      case Some(user) => lookupUserAddress(user).flatMap{
        case None => Left(Error("***"))
        case Some(add) => demandOnAddress(add)
      }
    }