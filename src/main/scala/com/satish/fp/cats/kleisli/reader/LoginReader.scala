package com.satish.fp.cats.kleisli.reader

import cats.data.Reader
import cats.syntax.applicative.*

object LoginReader:
  final case class DB(username: Map[Int, String], password: Map[String, String])

  type DbReader[A] = Reader[DB, A]

  def findUsername(id: Int): DbReader[Option[String]] =
    Reader(db => db.username.get(id))

  def checkPassword(username: String, password: String): DbReader[Boolean] =
    Reader(db => db.password.get(username).contains(password) )

  def checkLogin(id: Int, password: String): DbReader[Boolean] =
    for{
      username <- findUsername(id)
      passwordMatch <- username.map{
        un => checkPassword(un, password)
      }.getOrElse(false.pure[DbReader])
    } yield passwordMatch

object loginReaderMain extends App:
  import LoginReader.*
  val users = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )
  val passwords = Map(
    "dade"  -> "zerocool",
    "kate"  -> "acidburn",
    "margo" -> "secret"
  )
  val db = DB(users, passwords)
  println(checkLogin(1, "zerocool").run(db))
  println(checkLogin(4, "davinci").run(db))




