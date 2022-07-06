package com.satish.fp.cats.error
import cats.{MonadThrow, MonadError}
import cats.syntax.all.*
import cats.instances.all.*

case class Person(name: String, age: Int)

case class Address(street: String, pin: String)

object MonadErrors extends App:

  type MonadStringError[F[_]] = MonadError[F, String]
  def getPerson[F[_] : MonadStringError](id: String): F[Person] =
    if id == "123" then Person("Joe", 22).pure
    else "does not exist".raiseError

  def getAddress[F[_] : MonadStringError](p: Person): F[Address] =
    Address("abc", "221234").pure


  def program[F[_] : MonadStringError](id : String) : F[Address] =
    val address = for{
      p <- getPerson(id)
      a <- getAddress(p)
    }yield a

    address.handleError{
      case _ => Address("null", "null")
    }

  println(program("123"))
  println(program("1234"))