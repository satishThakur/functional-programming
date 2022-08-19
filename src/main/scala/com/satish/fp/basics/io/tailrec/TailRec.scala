package com.satish.fp.basics.io.tailrec

import com.satish.fp.basics.io.Monad


enum TailRec[A]:

  case Return(a: A)

  case Suspend(resume: () => A)

  case FlatMap[A, B](sub: TailRec[A], k: A => TailRec[B]) extends TailRec[B]

  def flatMap[B](f: A => TailRec[B]): TailRec[B] =
    FlatMap(this, f)

  def map[B](f: A => B): TailRec[B] =
    flatMap(a => Return(f(a)))

  @annotation.tailrec final def run: A = this match
    case Return(a) => a
    case Suspend(r) => r()
    case FlatMap(x, f) => x match
      case Return(a) => f(a).run
      case Suspend(r) => f(r()).run
      case FlatMap(y, g) => y.flatMap(a => g(a).flatMap(f)).run

object TailRec:
  def apply[A](a: => A): TailRec[A] =
    suspend(Return(a))

  def suspend[A](a: => TailRec[A]) =
    Suspend(() => a).flatMap(identity)

  given monad: Monad[TailRec] with
    def unit[A](a: => A): TailRec[A] = TailRec(a)

    extension[A] (fa: TailRec[A])
      def flatMap[B](f: A => TailRec[B]): TailRec[B] = fa.flatMap(f)

object TailRecApp extends App:
  val f : Int => Int = x => x

  val g = List.fill(100000)(f).foldLeft(f)(_ compose _)

  //println(g(42)) - will cause stack overflow as we have f(f(f(..)))
  import TailRec.*
  val tf : Int => TailRec[Int] = x => Return(x)

  //Kliesli composition!!
  val gf : Int => TailRec[Int] = List.fill(100000)(tf).foldLeft(tf)((tf1, tf2) => x => suspend(tf1(x).flatMap(tf2)))

  println(gf(42).run)
