package com.satish.fp.cats.monad.transformer

import cats.data.EitherT

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object Autobots:
  type Response[A] = EitherT[Future,String, A]
  val powerLevels = Map(
    "Jazz"      -> 6,
    "Bumblebee" -> 8,
    "Hot Rod"   -> 10
  )
  def getPowerLevel(autobot: String): Response[Int] =
    EitherT(Future(powerLevels.get(autobot).toRight("Not found")))
  /*
  powerLevels.get(autobot) match {
    case None => EitherT.left(Future("not enough fuel"))
    case Some(x) => EitherT.right(Future(x))
  }
  */

  // Can only move if power sum is more than 15
  def canSpecialMove(ally1: String, ally2: String): Response[Boolean] = for{
    x <- getPowerLevel(ally1)
    y <- getPowerLevel(ally2)
  } yield (x + y > 15)


  def tacticalReport(ally1: String, ally2: String): String =
    val resp = canSpecialMove(ally1, ally2).value
    Await.result(resp, 5.second) match {
      case Left(x) => x
      case Right(true) => s"$ally1 and $ally2 are ready to blast"
      case Right(fale) => s"$ally1 and $ally2 need more fuel..."
    }

object AutoBotsMain extends App:
  import Autobots.*
  println(tacticalReport("Jazz", "Bumblebee"))

  println(tacticalReport("Bumblebee", "Hot Rod"))

  println(tacticalReport("Jazz", "Ironhide"))
