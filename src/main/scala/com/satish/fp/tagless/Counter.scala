package com.satish.fp.tagless

import cats.effect.{MonadCancelThrow, Ref, Resource}
import dev.profunktor.redis4cats.algebra.StringCommands
import cats.Monad
import cats.syntax.all.*

trait Counter[F[_]]:
  def get: F[Int]
  def incr: F[Unit]

object Counter:
  def make[F[_] : MonadCancelThrow]
  (key: String, commandResource : Resource[F,StringCommands[F, String, Int]]): Counter[F] =
    new Counter[F]:
      override def get: F[Int] =
        commandResource.use(_.get(key).map(_.getOrElse(0)))

      override def incr: F[Unit] = commandResource.use(_.incr(key).map(_ => ()))

  def makeTest[F[_]](ref : Ref[F,Int]): Counter[F[_]] = new Counter[F]:
    override def get: F[Int] = ref.get
    override def incr: F[Unit] = ref.update(_ + 1)