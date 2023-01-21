### Introduction
HTTP4S is a Scala library which implements Typeful and functional HTTP server and client. 
HTTP4S is built on top of Cats Effect and Cats. It is a pure functional library which is type safe and composable.
HTTP4S uses FS2 for streaming and Cats effect for concurrency. This document would outline the high level 
design of HTTP4S and how it can be used to build a functional HTTP server.

### Core Constructs
