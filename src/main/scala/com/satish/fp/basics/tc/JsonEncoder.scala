package com.satish.fp.basics.tc

import scala.deriving.*
import scala.compiletime.{constValue, erasedValue, summonFrom, summonInline}

/**
 * TypeClass definition for encoding to JSON string.
 * @tparam A
 */
trait JsonEncoder[A]:

  def encodeJson(a: A) = a.encode

  extension(a : A)
    def encode: String

end JsonEncoder

inline def elemLabels[T <: Tuple] : List[String] = inline erasedValue[T] match
  case _: EmptyTuple => Nil
  case _: (t *: ts) => constValue[t].asInstanceOf[String] :: elemLabels[ts]


inline def summonAll[A <: Tuple]: List[JsonEncoder[_]] = inline erasedValue[A] match
  case _: EmptyTuple => Nil
  case _: (t *: ts) => summonInline[JsonEncoder[t]] :: summonAll[ts]

object JsonEncoder:
  inline given derived[A](using m: Mirror.Of[A]) : JsonEncoder[A] =
    new JsonEncoder[A]:
      extension(a: A)
        def encode: String = inline m match
          case s: Mirror.SumOf[A]     => deriveSum(s,a)
          case p: Mirror.ProductOf[A] => deriveProduct(p,a)

  inline def deriveCase[A,T <: Tuple](pos : Int, ord: Int, a: A) : String = inline erasedValue[T] match
    case _: EmptyTuple => ""
    case _: (t *: ts) => {
      if pos == ord then
        summonFrom{
          case p: Mirror.ProductOf[`t`] => deriveProduct[t](p, a.asInstanceOf[t])
        }
      else deriveCase[A, ts](pos + 1, ord, a)
    }

  inline def deriveSum[A](s: Mirror.SumOf[A], a: A): String =
    val ord = s.ordinal(a)
    deriveCase[A, s.MirroredElemTypes](0, ord, a)

  inline def deriveProduct[A](p: Mirror.ProductOf[A], a: A): String =
    val instances = summonAll[p.MirroredElemTypes]
    val names: List[String] = elemLabels[p.MirroredElemLabels]
    val values: List[Any] = a.asInstanceOf[Product].productIterator.toList
    val fields: List[String] = (names zip (instances zip values)) map {
      case (name, (instance, value)) => s"\"$name : ${instance.asInstanceOf[JsonEncoder[Any]].encode(value)}"
    }
    fields.mkString("{", ",", "}")

  given stringEncoder : JsonEncoder[String] with
    extension(a: String)
      def encode: String = s"\"${a}\""

  given intEncoder: JsonEncoder[Int] with
    extension(a: Int)
      override def encode: String = a.toString

  given listEncoder[A](using e : JsonEncoder[A]): JsonEncoder[List[A]] with
    extension(l : List[A])
      def encode: String =
        val fields : List[String] = l.map(_.encode)
        fields.mkString("[", ",", "]")

end JsonEncoder