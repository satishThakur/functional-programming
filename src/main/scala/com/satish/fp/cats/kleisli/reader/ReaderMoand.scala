package com.satish.fp.cats.kleisli.reader

import cats.data.Reader

object ReaderMonad extends App:
  case class Person(name: String, age: Int, salary: Int)

  val nameReader: Reader[Person, String] = Reader(p => s"Name is ${p.name}")
  println(nameReader.run(Person("Rey", 8, 0)))

  val ageReader: Reader[Person, String] = Reader(p => s" age is ${p.age}")

  val salaryReader: Reader[Person, String] = Reader(p => s" and salary is ${p.salary}")

  val personReader = for{
    n <- nameReader
    a <- ageReader
    s <- salaryReader
  } yield (n + a + s)

  println(personReader.run(Person("john", 40, 10000)))
