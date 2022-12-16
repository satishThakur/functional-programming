package com.satish.fp.basics.effects

object Composition extends App:
  type A
  type B
  type C

  val f : A => Option[B] = ???
  val g : B => Option[C] = ???
  //does not compose
  //f andThen g //type error
/**
  type F[A] = Option[A]
  type F[A] = Either[E, A] //for some fixed type E
  type F[A] = List[A]
  type F[A] = Reader[C, A] //for some fixed Config C
  type F[A] = State[S,A] //For some fixed type S

**/