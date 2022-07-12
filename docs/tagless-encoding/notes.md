## Tagless Final Encoding
Tagless Final Encoding in Scala context is a way to use effect system and program to interface. Lets uncover the parts of the pattern.

### Algebra
Algebra is nothing but the interface which abstract over the effect. Lets take simple example of a Counter:
``` scala
trait Counter[F[_]]:
    def get: F[Int]
    def incr: F[Unit]
```
Here we define the Counter Algebra (operation) abstracting over the effect.
*Note* - Though the Algebra looks similar to ***TypeClass*** pattern, it has nothing to do with typeclass. A typeclass have set of coherent instance types (typically one for each type). But Algebra has multiple implementations, implemented either using subclass or typeclass.

On similar lines Algebra for Items would looks like:
```scala
trait Items[F[_]]:
  def getAll: F[List[Item]]
  def get(id: ItemId): F[Item]
  def add(item: Item): F[Unit]
```
So far nothing strange right, It is just an interface which is defining the operations and abstracting over effect.
Few things to node:
* The effect has no context bound (no typeclass constraints). As this is just contract there should be no need to have any context bound for effect.

### Interpreters
We would generally have two implementations called Interpreters. One for testing and one for production. The interpreters can be implemented using concrete effects such as `IO` or polymorphically as described below:
```scala
object Counter:
  def make[F[_] : Functor](key: String, command : StringCommands[F, String, Int]): Counter[F] = new Counter[F]:
    override def get: F[Int] = command.get(key).map(_.getOrElse(0))
    override def incr: F[Unit] = command.incr(key).map(_ => ())

  def makeTest[F[_]](ref : Ref[F,Int]): Counter[F[_]] = new Counter[F]:
    override def get: F[Int] = ref.get
    override def incr: F[Unit] = ref.update(_ + 1)
```

As we see we use Redis for production use case but use in-memory counter for testing. 

###Programs
Programs are the one where we implement business logic and use the algebra defined using Tagless Final pattern. For example lets say we want to increase counter when we retrieve items, the program would look like:
```scala
class ItemCounters[F[_]: Apply](items: Items[F], counter: Counter[F]):
  def getItems: F[List[Item]] = counter.incr *> items.getItems
```
Simple program could also be implemented as functions. 

###Why Tagless Finals?
Alternative is to code using concrete `IO` but at a cost:
* Lost of parametricity and principle of least power.
* Constraints leads to reduced chances of error
