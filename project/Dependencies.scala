import sbt._

object Dependencies {
  object V {
    val cats       = "2.7.0"
    val catsEffect = "3.3.12"
    val catsRedis = "1.2.0"
  }
  object Libraries {
    val cats         = "org.typelevel"  %% "cats-core"          % V.cats
    val catsEffect   = "org.typelevel"  %% "cats-effect"        % V.catsEffect
    val catsRedis    = "dev.profunktor" %% "redis4cats-effects" % V.catsRedis
  }
}

