## Higher Kinded Types
Let's start with the basic types like `Int` or `String`. There are normal or proper types which we can directly use to define values such as:

`val x: Int = 4`

`val p : Person = Person("John", "25")`

Now lets move the abstraction one level up. We have parameterized types or ***Type Constructors***. 

A **Type Constructor** takes one or more types as arguments and produces a new type. This would be clear with an example:

* `List` is a **Type Constructor** which is defined in Scala as a Parametrized Class. Note that `List` is ***not*** a Type.
* `List[String]` is a type - we provide `String` as the input and now we have a new type.

Hence `List` is a type constructor which takes a type and gives us another type. This sounds very familiar with functions for values.

`type myList = [X] =>> List[X]` - This defines a new type myList which provided a Type `X` produces a new type called `List[X]`. That's exactly is the definition of `List`. Hence when viewed as a type the type of the `List` would be `[X] =>> List[X]`. This syntax is called `Type Lambda`. 

### Higher Kinded Types
Now lets take the abstraction one level up. What if we want to abstract over the type constructor's itself?

HKT takes a **Type Constructor** as input and provide a new Type. This would be more clear with an example.

We have seen `map` as a function appear in many containers/collections. Now lets say we want to abstract over `map`. We could write something like:
```scala
trait Mapper[F[_]]:
  def map[A,B](fa: F[A])(f : A=> B): F[B]
```

Where F is a type Constructor. The way to read this is Mapper is a HKT which takes Type Constructor as input to product a new Type.

Here `Mapper[List]` would be a type. Now we can write a generic and polymorphic API using such abstraction. For example:
```scala
def doubleNums[F[_]](nums: F[Int], mapper : Mapper[F]): F[Int] = mapper.map(nums)(_ * 2)
```
The `doubleNums` works with any type constructor which has a corresponding Mapper defined. The API looks weird, but we will clean it uo when we reach "Type Classes". 
The code for this section is [here]("https://github.com/satishThakur/functional-programming/blob/main/src/main/scala/com/satish/fp/basics/HKT.scala")