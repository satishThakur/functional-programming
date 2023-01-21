## Motivation for Functional Programming

### Complexity Management
Bulk of our time is spent in managing Complexity of the Systems. If we have to summarize what we are tying to solve:
How do we build systems which functions correctly and keep on doing so when modified.
As we add more code/features to the system if the complexity increases linearly the speed of execution as well as operatiblity would soon become challenge. We do want to create a system which is complex in terms of features and functionalies but not in terms of Code. The code should be simple, modular, easy to reason about and extend.

### Fearless Refactoring
As we add more code/features to a system ability to do continous refactoring is a must have. Missed oppurtunities for refactoring build more and more technical debt leading to complex code base which is difficult to understand and extend. 

### Local reasoning
One of the most important property we need is "Local Reasoning". If we can reason about a piece of code (could be a function, class, module) locally - without looking at other part of the code then this helps us in managing the complexity. 
We can add more code and as long as various part of our application have `Local Reasoning` the complexity is contained. 

### Easy to test and debug.

Explain what functional programming is and how it can help in few of these areas.

Sound mathematical foundations to functional programming - what does that give us?
### Referential Transparency and Substitution Model - Holy grail
Referential transparency simple means we can replace the expressions with their values. So an Expression E is RT if every accurance of E we can replace with the value V.
Similarly a function is RT if we can substitude the function invocation with the value. 
RT gives us ability to use Substitution model for evaluation - which is nothing but local reasoning. We can treat our program like Mathematical expressions and reason about their behaviour locally.


