package com.satish.fp.basics.extractors

object Foo{
  def unapply(x: Int): Option[String] = if x > 10 then Some(s"$x") else None
}

object ->{
  def unapply(x : Int): Option[(Int, String)] = if x > 10 then Some((x, s"$x")) else None
}


object Sample extends App:

  15 match{
    case Foo(str) => println(str)
    case _ => println("unmatched..")
  }


  20 match {
    case x -> str => println(s"$x , $str")
    case _ => println("unmatched")
  }

