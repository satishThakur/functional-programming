package com.satish.fp.basics.purefn

import scala.util.Random

/**
 * Pure function is one which is total and totally deterministic.
 * Total means for every input value it should have an output value.
 * Deterministic means for same input it should result in same values.
 * Pure Function must not have side effects. This has link with RT as well.
 */
object PureFn extends App:

  //Is this pure function?
  //Option[T]
  def divide(x: Int, y : Int): Option[Int] = if y == 0 then None else Some(x/y)

  //Either[T, U]

  type ErrorString[A] = Either[String, A]

  //[X], 0 ---> None



  //is this pure?
  //what about RT?
  def random = new Random(31).nextInt()

  //is this function pure?
  //Also is this RT
  //total  - Yes
  //deterministic - Yes
  def add(a : Int, b: Int): Int =
    val sum = a + b
    println(s"sum is $sum")
    sum

  val num1 = 100
  val num2 = 50

  val num3 = add(num1, num2)
  //val num4 = add(num1, num2)

  println(num3 + num3)
