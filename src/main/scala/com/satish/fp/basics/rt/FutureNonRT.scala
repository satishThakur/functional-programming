package com.satish.fp.basics.rt

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object FutureDemo:
  def version1 =
    val f = for {
      x <- Future {println("foo")}
      y <- Future {println("foo")}
    } yield ()

    val fiveSeconds = Duration(5, "seconds")
    val r: Unit = Await.result(f, fiveSeconds)
    println(r)
    println("****end of first part****")

  def version2 =
    val printFuture = Future {println("foo")}
    val g = for {
      x <- printFuture
      y <- printFuture
    } yield ()
    val fiveSeconds = Duration(5, "seconds")
    val r2: Unit = Await.result(g, fiveSeconds)
    println(r2)


object FutureNonRT extends App:
  FutureDemo.version1
  FutureDemo.version2
