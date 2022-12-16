package com.satish.fp.basics.monad

/**
Build an idea from evaluating an simple expression to Monad
**/

trait Exp

case class Value(a : Int) extends Exp

case class Div(l : Exp, r : Exp) extends Exp

// 4 / 5 ==> Div(Value(4), Value(5))

object Exp:
  /**
   * First attemp but is Eval a total function?
   *
   */
  def eval(e: Exp): Int = e match{
    case Value(i) => i
    case Div(l,r) => eval(l) / eval(r)
  }


  def saveDivision(a: Int, b: Int ): Option[Int] = if b == 0 then None else Some(a/b)

  /**
   *
   * Lets make our function a total function. But have we lost on the expressiveness?
   * Is this readable?
   */
  def safeEval(e: Exp) : Option[Int] = e match{
    case Value(i) => Some(i)
    case Div(l ,r) => (safeEval(l), safeEval(r)) match {
      case (None, _) => None
      case (_, None) => None
      case (Some(lv), Some(rv)) => Some(lv/rv)
    }
  }

  /**
   *
   * One more iteration but still difficult to read/understand.
   */
  def saveEvalBetter(e: Exp) : Option[Int] = e match {
    case Value(i) => Some(i)
    case Div(l, r) => saveEvalBetter(l).flatMap(
      lv => saveEvalBetter(r).map(rv => lv / rv)
    )
  }

  /**
   *
   * Lets get our expressiveness back!!
   */
  def saveEvalFinal(e: Exp): Option[Int] = e match {
    case Value(i) => Some(i)
    case Div(l, r) => for{
      lv <- saveEvalFinal(l)
      rv <- saveEvalFinal(r)
      result <- saveDivision(lv, rv)
    } yield result

  }
    
  

// a => Option[B], b => Option[C] --> a => Option[C]
