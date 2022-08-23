package com.satish.fp.basics.io

import com.satish.fp.basics.io
import com.satish.fp.basics.io.par.Nonblocking.*
import java.util.concurrent.{ExecutorService, Executors}

import scala.util.Try

enum Console[A]:

  case ReadLine extends Console[Option[String]]
  case  PrintLine(s : String) extends  Console[Unit]

  def toThunk: () => A = this match
    case ReadLine => () => Try(scala.io.StdIn.readLine()).toOption
    case PrintLine(s) => () => println(s)

  def toPar: Par[A] = this match
    case  ReadLine => Par.lazyUnit(Try(scala.io.StdIn.readLine()).toOption)
    case PrintLine(s) => Par.lazyUnit(println(s))


object Console:
  import Free.*
  def readLn : Free[Console, Option[String]] = Suspend(ReadLine)
  def printLn(s: String) : Free[Console, Unit] = Suspend(PrintLine(s))

  extension [A](fa: Free[Console, A])
    def toThunk: () => A =
      fa.runFree([x] => (c: Console[x]) => c.toThunk)

    def toPar: Par[A] =
      fa.runFree([x] => (c: Console[x]) => c.toPar)

    def unsafeRunConsole: A =
      fa.translate([x] => (c: Console[x]) => c.toThunk).runTrampoline

object ConsoleApp extends App:


  val name : Free[Console, Option[String]] = for{
    _ <- Console.printLn("Enter your name please")
    in <- Console.readLn
  }yield in

  println(name.unsafeRunConsole)


  //val es = Executors.newFixedThreadPool(3)

  //val pv : Par[Option[String]] = name.toPar

  //println(pv.run(es).get)







