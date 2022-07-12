package com.satish.fp.tagless

import cats.effect.Ref
import dev.profunktor.redis4cats.algebra.StringCommands
import cats.Functor
import cats.syntax.all.*

trait Counter[F[_]]:
  def get: F[Int]
  def incr: F[Unit]

object Counter:
  def make[F[_] : Functor](key: String, command : StringCommands[F, String, Int]): Counter[F] = new Counter[F]:
    override def get: F[Int] = command.get(key).map(_.getOrElse(0))
    override def incr: F[Unit] = command.incr(key).map(_ => ())

  def makeTest[F[_]](ref : Ref[F,Int]): Counter[F[_]] = new Counter[F]:
    override def get: F[Int] = ref.get
    override def incr: F[Unit] = ref.update(_ + 1)