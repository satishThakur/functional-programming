package com.satish.fp.application.program
import com.satish.fp.application.domain.Goal
import cats.Applicative
import cats.syntax.applicative.*

trait Goals[F[_]]:
  def getAll: F[List[Goal]]


object Goals:
  def make[F[_] : Applicative] : Goals[F] = new Goals[F]:
    override def getAll: F[List[Goal]] =
      List(Goal("dummy", "open"),
      Goal("something else", "closed")).pure[F]

