### Skunk
Skunk is a Scala functional PostgreSQL client built on top of Cats Effect and fs2. It is a pure functional library which is type safe and composable. Skunk uses fs2 for streaming and Cats effect for concurrency. This document would outline the high level design of Skunk and how it can be used to build a functional PostgreSQL client.
Skunk is non-blocking and async from grounds up. Skunk is not built on top of JDBC but rather built on
 postgres protocol. Skunk is a pure functional library which is type safe and composable. Skunk uses fs2 for streaming and Cats effect for concurrency. This document would outline the high level design of Skunk and how it can be used to build a functional PostgreSQL client.
