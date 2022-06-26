package com.satish.fp.basics

/**
 * Code sample to build an intuition towards `Higher Kinded Types` in Scala
 */

//HKT - We want to abstract something around Map - We see Map is common function for collection/containers.

trait Mapper[F[_]]:
  def map[A,B](fa: F[A])(f : A=> B): F[B]

object Mapper:
  val listMapper = new Mapper[List]:
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa map f

  val optionMapper = new Mapper[Option]:
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa map f




object HKT extends App:

  val x : Int = 4 //Normal Types or first order types. There are fully qualified.

  //val y : List = null - Will not compile as List is type constructor and hence need another type

  //Here we have provided String as a type to List - to get List[String]
  val names : List[String] = List("John", "Marry")

  type myList = [X] =>> List[X]

  type placeType = myList[String]

  val places : placeType = List("India", "spain")

  def doubleNums[F[_]](nums: F[Int], mapper : Mapper[F]): F[Int] = mapper.map(nums)(_ * 2)

  val num : Option[Int] = Some(5)

  val y : List[Int] = List(1,2,3,4)

  println(doubleNums(num, Mapper.optionMapper))
  println(doubleNums(y, Mapper.listMapper))

