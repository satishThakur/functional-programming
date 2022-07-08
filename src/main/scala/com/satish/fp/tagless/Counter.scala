package com.satish.fp.tagless

trait Counter[F[_]]:
  def get: F[Int]
  def incr: F[Unit]
