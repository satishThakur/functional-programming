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
