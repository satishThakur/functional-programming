package com.satish.fp.cats.error

import cats.effect.Sync
import cats.{MonadThrow, Functor, ApplicativeThrow}
import cats.syntax.all.*

import scala.util.control.NoStackTrace

case class Category(name: String)

trait Random[F[_]]:
  def bool: F[Boolean]
  def int: F[Int]

object Random:
  def apply[F[_]: Random]: Random[F] = summon

  given syncRandom[F[_] : Sync] : Random[F] with
    override def int: F[Int] = Sync[F].delay(scala.util.Random.nextInt())
    override def bool: F[Boolean] = int.map(_ %2 == 0)

sealed trait BusinessError extends NoStackTrace
case object RandomError extends BusinessError
case object AnotherEror extends BusinessError

trait Categories[F[_]]:
  def findAll: F[List[Category]]
  def maybeFindAll: F[Either[BusinessError, List[Category]]]

/**
 *
 * Lets decode the magic of Random[F].bool.map here -
 * Random[F] is a call to Random.apply which gives us Random[F] instance.
 * Note that call to apply needs typeclass instance of Random[F] (for summon) - and we have it here via F[_]: MonadThrow: Random.
 * Now once we have instance of Random[F] we can call bool to get F[bool]
 * But how can we call map on it? By providing context bound for MonadError - we have Monad.
 * So we have Monad[F] - and cats via syntax will provide as F.map... Mystery solved!!
 */
class LiveCategories[F[_]: MonadThrow: Random] extends Categories[F]:

  override def findAll: F[List[Category]] = Random[F].bool.flatMap{
    case true => List(Category("shirt"), Category("shoes")).pure
    case false => RandomError.raiseError
  }

  override def maybeFindAll: F[Either[BusinessError, List[Category]]] = Random[F].bool.map{
    case true => Right(List(Category("shirt"), Category("shoes")))
    case false => Left(RandomError)
  }


class EitherProgram[F[_] : Functor](categories: Categories[F]):
  def findAll : F[List[Category]] = categories.maybeFindAll.map{
    case Left(_) => List.empty
    case Right(v) => v
  }

class Program[F[_] : ApplicativeThrow](categories: Categories[F]):
  def findAll : F[List[Category]] = categories.findAll.recover{
    case e => List.empty
  }



object ErrorHandling extends App{
  println("HT")

}
