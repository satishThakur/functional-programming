package com.satish.fp.tagless
import cats.Applicative
import cats.syntax.applicative.*
case class Item(name: String, color: String)

trait Items[F[_]]:
  def getItems: F[List[Item]]


object Items:
  def make[F[_]: Applicative]: Items[F] = new Items[F]:
    override def getItems: F[List[Item]] = List(Item("dummy", "red"), Item("pen", "black")).pure[F]

