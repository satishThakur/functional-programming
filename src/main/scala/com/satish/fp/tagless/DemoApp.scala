package com.satish.fp.tagless
import cats.effect.{ExitCode, IO, IOApp, Resource}
import dev.profunktor.redis4cats.*
import dev.profunktor.redis4cats.algebra.StringCommands
import dev.profunktor.redis4cats.connection.*
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.log4cats.*
import dev.profunktor.redis4cats.effect.Log.Stdout.*
import dev.profunktor.redis4cats.codecs.Codecs
import dev.profunktor.redis4cats.codecs.splits.*
import dev.profunktor.redis4cats.data.RedisCodec

object DemoApp extends IOApp:
  def application(itemCounter : ItemCounters[IO], counter: Counter[IO], count: Int): IO[Int] =
    for{
      _ <- itemCounter.getItems
      c <- if count == 0 then counter.get else application(itemCounter, counter, count -1)
    }yield c

  override def run(args: List[String]): IO[ExitCode] =
    val stringCodec: RedisCodec[String, String] = RedisCodec.Utf8
    val redisClient : Resource[IO, RedisClient] = RedisClient[IO].from("redis://localhost")

    val newCodec: RedisCodec[String, Int] =
      Codecs.derive(RedisCodec.Utf8, stringIntEpi)
    val stringCommand: Resource[IO, StringCommands[IO, String, Int]] =
      redisClient.flatMap(Redis[IO].fromClient(_, newCodec))

    val items = Items.make[IO]
    val counter = Counter.make[IO]("item-counter", stringCommand)
    val itemCouner = new ItemCounters(items, counter)
    application(itemCouner, counter, 10)
      .flatMap(i => IO.println(s"Counter - $i")).as(ExitCode.Success)
