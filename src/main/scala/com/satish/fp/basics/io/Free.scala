package com.satish.fp.basics.io

enum Free[F[_], A]:

  case Return(a: A)

  case Suspend(resume: F[A])

  case FlatMap[F[_], A, B](s: Free[F, A], f: A => Free[F, B]) extends Free[F, B]

  def flatMap[B](f: A => Free[F, B]): Free[F, B] = FlatMap(this, f)

  def map[B](f: A => B): Free[F, B] = flatMap(a => Return(f(a)))

  //Free interpreter which works for any Monad
  //Note that this is not tailrec
  final def run(using F: Monad[F]): F[A] = step match {
    case Return(a) => F.unit(a)
    case Suspend(fa) => fa
    case FlatMap(Suspend(fa), f) => fa.flatMap(a => f(a).run)
    case FlatMap(_, _) => sys.error("Impossible, since `step` eliminates these cases")
  }

  @annotation.tailrec
  final def step: Free[F, A] = this match
    case FlatMap(FlatMap(fx, f), g) => fx.flatMap(x => f(x).flatMap(y => g(y))).step
    case FlatMap(Return(x), f) => f(x).step
    case _ => this

  def runFree[G[_]](t: [x] => F[x] => G[x])(using G: Monad[G]): G[A] = step match
    case Return(a) => G.unit(a)
    case Suspend(r) => t(r)
    case FlatMap(Suspend(r), f) => t(r).flatMap(a => f(a).runFree(t))
    case FlatMap(_, _) => sys.error("Impossible, since `step` eliminates these cases")

  def translate[G[_]](ftog: [x] => F[x] => G[x]): Free[G, A] =
    runFree([x] => (fx: F[x]) => Suspend(ftog(fx)))


object Free:
  given freeMonad[F[_]]: Monad[[x] =>> Free[F, x]] with
    override def unit[A](a: => A): Free[F, A] = Return(a)

    extension[A] (fa: Free[F, A])
      def flatMap[B](f: A => Free[F, B]): Free[F, B] = fa flatMap (f)

  extension[A] (fa: Free[Function0, A])
    @annotation.tailrec
    def runTrampoline: A = fa match
      case Return(a) => a
      case Suspend(ta) => ta()
      case FlatMap(fx, f) => fx match
        case Return(x) => f(x).runTrampoline
        case Suspend(tx) => f(tx()).runTrampoline
        case FlatMap(fy, g) => fy.flatMap(y => g(y).flatMap(f)).runTrampoline
