package com.satish.fp.tokenauth

import java.util.UUID
import io.circe.Codec

case class UserClaim(userid: UUID) derives Codec.AsObject
