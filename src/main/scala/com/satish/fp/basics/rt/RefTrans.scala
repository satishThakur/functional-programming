package com.satish.fp.basics.rt

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RefTrans:

  def sum(a : Int, b : Int) : Int = a + b

  def futureTask[T](task: => T) : Future[T] = Future(task)


object MainApp extends App:
  println("hello world!!")