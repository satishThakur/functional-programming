package com.satish.fp.cats.kleisli
import cats.data.Kleisli

object Examples extends App:

    def f : Int => Option[String] = _ => Some("abc def")

    def g : String => Option[List[String]] = a => Some(a.split(" ").toList)

    //we can not compose these functions ...

    val h = Kleisli(f).andThen(Kleisli(g))

    val result : Option[List[String]] = h.run(1)
    println(result)


