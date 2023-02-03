package com.satish.fp.tokenauth
import java.util.UUID
import cats.effect.{IO, IOApp}
import cats.syntax.all.*

object TokenApp extends IOApp.Simple:
  override def run: IO[Unit] =
    val token = Token.makeSymToken[IO,UserClaim](SecretKey("superSecret"))
    val user = UserClaim(UUID.randomUUID())
    val anotherUser = UserClaim(UUID.randomUUID())
    val privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCz281T2SID8R+k\nUyNPySumwh+pC8NiaM6eZKXqFdBRQXJZGQBe6safPttnsTtCL4prC0rVPdCMhL9P\nEPaXz3mfuoJmj+l5sHxErbLqw9DAACOTHSoeFMzqsvgobwnRv7O/XNmRlIRbNFwU\nWETkRVWqqx3wXZ7l/nEdvdh5yLwW/2aYCJfNslJho+l4WlhqukSKnj/9DI6+XGB/\n900IX8zhi1H7ZulTkLqKZhuXYVvKbsi4u5gi1MDSF1Mh5ZEhJd0bUKniByAHILC7\nAO+h/Q8ehbVOgGLBpsN1HLpgZa/o2PIkl4zGKpkn0UuKIq8J5MN8K3rqEbbkOXf3\nO3EZWNVfAgMBAAECggEABOPDwCKsyOxdQQAhn+rQMZBqEssHE6uMoDHDJ9RSRqxk\nQJfZ+98xjgmfb67QrHiJ1Rzjw+822xsgyqIRJHZJ0DFMKkSBnrnjS5GNWEABcpJF\nQMBZFezYjnsxZjpMSy+FUA+uKEXpYPqIAcEElVAkGcxK3BPByrgRSzNqqYBcuCNL\ncxP8A6IJoEXJZPAZZqxztflI6YlON6FYjViyhgTz2gMFPrFjH99TbXc9/q2accA9\n4Al/lTC387Yyk8cVfsDqDriWrGprXmPecHx9lygzx5VMs0XD4SR1JirdNILUWFKl\nuxkizjUgEn8u3HTqswM+j2BTvosk7U92CRrcA4BYAQKBgQDRoyqYkIVc3LLOZLm5\nZreDYOl2Yah75CIZJOIWOxHx50myUIWkTn53pQRdNAbf1u75+ttudyY/O8u9oCxF\nWgtbT72rXXUwfSPr/+LkUu8oMJ500s7vdlIjtX8KKm8C4UrDZr0N+KRnY6ZnzDXV\nWvbuYwsyXAOhuoMt1gDVFkbP3wKBgQDboq4tw8cNKWXySkIcpZJCC8JpofgPbxAN\nFe+hEIpX7J/8XOngLe93vUKKI/HjAJQPEjRKs1srugevAOkP0v2nc4HMX01T6vrC\nyp/hZ4KPb/cOpWBKEiNHWx5vcECcUewPiaJPHNi1rfE4xW215Wm/X+U2fXcrbgyC\nsz7M2mCqgQKBgGkNxWjS86IHjytS2qeIS+pwhE4ovk/nkRbcntfZHjMFXq8XHwGp\nvEKk1T0Ht3IwuW1YQuJmnyoNAxqxy72tVAecPEZF/VYhQAEiKEeXL/YJ9z4/7iee\nOadfapxji2H8GIU3VQJNWcXd8CQP9+JFRX1M8O15ovBHETnFBoHCOhYtAoGBAMVb\nG9ohppYsEeAyW8+z84WHXY1fri/oI2suv+FhpH43MNcqgjkf7aMRnF3WyL6qwV/9\ngqFxIsZa5haZ4dKHS8gQ4ZxMMobqiaNJQXrgcRAEkuJFNg242JyAwwttuZD9h/m6\nOe+OwygVgcD6noeo+mmteKCLAu8ydulmuVKIE/6BAoGBAK+7Qvg2JoprfkB7CAio\n7h+cRG2bzda+WXH/hOLl7I/dGH9hv2wlvh7z61zwfRzkFhkExUlUH1v0fOFnzzh+\nX56gcVB1h1yVU0LkvS7dpdTzd4c3HruOV//Cw70uvZO17Its8yil3640ltH1m9om\nMFtIeOWAFO63+e+u8VifENDd\n-----END PRIVATE KEY-----"
    val publicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs9vNU9kiA/EfpFMjT8kr\npsIfqQvDYmjOnmSl6hXQUUFyWRkAXurGnz7bZ7E7Qi+KawtK1T3QjIS/TxD2l895\nn7qCZo/pebB8RK2y6sPQwAAjkx0qHhTM6rL4KG8J0b+zv1zZkZSEWzRcFFhE5EVV\nqqsd8F2e5f5xHb3Yeci8Fv9mmAiXzbJSYaPpeFpYarpEip4//QyOvlxgf/dNCF/M\n4YtR+2bpU5C6imYbl2Fbym7IuLuYItTA0hdTIeWRISXdG1Cp4gcgByCwuwDvof0P\nHoW1ToBiwabDdRy6YGWv6NjyJJeMxiqZJ9FLiiKvCeTDfCt66hG25Dl39ztxGVjV\nXwIDAQAB\n-----END PUBLIC KEY-----"
    val asymToken = Token.makeAsymToken[IO,UserClaim](AsymmetricKeyPair(privateKey, publicKey))
    println(user)
    println(anotherUser)

    val y = for{
      to <- asymToken.create(user)
      _ <- IO.println(to)
      user <- asymToken.verify(to)
      _ <- IO.println(user)
    }yield ()

    val x : IO[Unit] = for{
      t <- token.create(user)
      _ <- IO.println(t)
      user <- token.verify(t)
      _ <- IO.println(user)
    } yield ()

    y.flatMap(_ => x)