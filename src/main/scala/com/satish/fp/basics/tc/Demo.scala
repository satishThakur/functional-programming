package com.satish.fp.basics.tc

import com.satish.fp.basics.tc.JsonEncoder.given
case class Person(name: String, age: Int) derives JsonEncoder

enum SKU derives JsonEncoder:
  case Shirt(brand: String, price: Int)
  case Coffee(taste: String, price: Int)

object DemoApp extends App:
  import JsonEncoder.given
  println("hello".encode)
  println(1234.encode)
  println(Person("John", 25).encode)

  val persons = List(Person("Mary", 22), Person("Harry", 50))
  println(persons.encode)

  val p : SKU = SKU.Shirt("lf", 123)

  println(p.encode)
