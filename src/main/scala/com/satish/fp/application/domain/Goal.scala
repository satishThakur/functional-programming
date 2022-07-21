package com.satish.fp.application.domain

import io.circe.Codec
import io.circe.parser.decode
import io.circe.syntax.*

case class Goal(name: String, status : String) derives Codec.AsObject


