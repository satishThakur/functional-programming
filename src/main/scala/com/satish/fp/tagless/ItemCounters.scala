package com.satish.fp.tagless
import cats.Apply
import cats.syntax.apply.*

class ItemCounters[F[_]: Apply](items: Items[F], counter: Counter[F]):
  def getItems: F[List[Item]] = counter.incr *> items.getItems
