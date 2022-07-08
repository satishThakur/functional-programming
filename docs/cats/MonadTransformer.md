## Monad Transformers
As we know we use `Monad` for composing effects. Now lets think of use cases where we need to work with multiple effects at the same time. Let's take an example:

We are working in a domain where we need to authenticate the user. Basically we need do implement following business logic:
1. Check if user exists in our system. If not fail the authentication.
2. Check if password matches. 
3. Check user subscription. Error in case subscription is expired.
4. Check if user is active.

* Now here there would be few n/w operations (talking to db, external services) so we would like to use `IO` effect system for suspension etc.
* As these individual steps can fail hence we want to encode errors as well. We can use `Either` effect for the same.

Which means we would be using `IO` and `Either` effect at the same time. Now there are multiple examples which will have same patterns:
* Modeling Either with Option - `Either[Error, Option[User]]`
* Modeling Either with Future - `Future[Either[Error, A]]`

### What is Monad Transformer?
One way to solve the above problem would be by composing Monads. We have seen Applicative's compose which means:
If I have 2 monads `F` and `G` will `F[G]` or `G[F]` be a Monad as well?
The answer is no - there is nt generic way to implement Monad composability just by reling on Monad laws.
But...

If we know the concrete inner Monad we can generate Monad for any arbiratary outer Monad. Lets try this with Either as fixed inner Monad:

### Example How MonadTransformer simplify the code