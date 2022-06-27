# TypeClasses

When we are looking at *Polymorphism* there are few ways to achieve the same:
* **Method Overloading** - where same method takes different type as argument. 
* **Sub-Type** - Class Hierarchy and is generally suitable for related types.

**Sub-Type Polymorphism** has its own limitations few of them are:
* It is not possible to extend a Type always - for example already refined types in external libraries etc.
* Does not go well with variance as well. For example trait being defined as in-variant and the sub-class is co-variant etc.
* In case of unrelated types - **Subtype Polymorphism** is not ideal. For example trait called `Ordering`. 

**Ad-hoc Polymorphism** - Enables Polymorphism of unrelated types. TypeClass is a pattern which enables adhoc-polymorphism in Scala. TypeClass has nothing to do with OO class, here class means set.
* The trait defining the pattern is called TypeClass.
* The implementation of TypeClass via `given` are called TypeClass instances.

Benefits of TypeClass:
* Allows ad-hoc polymorphism - can create instances of already defined class as well.
* Enhancements - Defined new typeclass instance based on already defined TypeClass. This is very powerful idea.

### TypeClass Example
Let's take an example of JSON Encoding. As JSON encoding is a separate concern it does not make sense to implement it via subtype polymorphism. The API could look like:

`def encode[A](a: A) : String`
 To define a TypeClass we would define a trait like:
```scala
trait JsonEncoder[A]:
  extension(a : A)
    def encode: String
```
The `extension` method adds the `encode` method to the type itself. So rather than saying `JsonEncoder[Person].encode(p)` we can directly write `p.encode`.

Now we can provide instances of this TypeClass. For example for String:
```scala
given stringEncoder : JsonEncoder[String] with
    extension(a: String)
      def encode: String = s"\"${a}\""
```
To see how the enhancement works lets say we want to write encoder for `List`. If we already have `encoder` for `A` we can derive `encoder` for `List[A]`:

```scala
given listEncoder[A](using e : JsonEncoder[A]): JsonEncoder[List[A]] with
    extension(l : List[A])
      def encode: String =
        val fields : List[String] = l.map(_.encode)
        fields.mkString("[", ",", "]")
```
Infact Scala 3 provides way to generate TypeClasses called typeClass derivation. We will cover the TypeClass derivation in another topic but if you are interested the code shared below does that. It let compiler generate TypeClasses for any ADT - both sum type and product type.
[Link to the code](https://github.com/satishThakur/functional-programming/tree/main/src/main/scala/com/satish/fp/basics/tc)


