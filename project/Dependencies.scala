import sbt._







object Dependencies {
  object V {
    val cats       = "2.7.0"
    val catsEffect = "3.3.12"
    val catsRedis = "1.2.0"
    val catsRedisLog = "1.2.0"
    val http4sVersion = "1.0.0-M34"
  }
  object Libraries {
    val cats         = "org.typelevel"  %% "cats-core"           % V.cats
    val catsEffect   = "org.typelevel"  %% "cats-effect"         % V.catsEffect
    val catsRedis    = "dev.profunktor" %% "redis4cats-effects"  % V.catsRedis
    val catsRedisLog = "dev.profunktor" %% "redis4cats-log4cats" % V.catsRedisLog
    val httpsDsl     = "org.http4s"     %% "http4s-dsl"          % V.http4sVersion
    val httpsServer = "org.http4s"      %% "http4s-ember-server" % V.http4sVersion
    val httpsClient = "org.http4s"      %% "http4s-ember-client" % V.http4sVersion
  }
}

