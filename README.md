# Actors and messages

An actor is the most basic unit performing work in Akka.

Its main job is to receive and send messages.

Your systems should contain a lot of actors - think of it as a giant corporation (only *much* faster and more scalable).
Like in a corporation, there is tons of delegating work.

An actor does only one thing at a time.
It has a mailbox, in which messages are enqueued in a non-blocking fashion (so that the caller does not have to wait).

The enqueuing operation wakes up the dispatcher. It then sends the message to the actor, which processes it on the currently assigned thread.
The processing occurs within the actor's `receive` method - it is a *blocking* operation.
The completion of the work wakes up the dispatcher again, which pulls the next message from the queue.

![Mailbox](Actor.png)

You define what the actor should respond to in the `receive` method.
It's important to know that the type of this method is `PartialFunction[Any, Unit]`, which effectively makes the actor an untyped construct.

```
case class Sun(sunrays: Int)
case class Snow(snowflakes: Int)

class Dog extends Actor {
    def receive = {
       case Sun(sunrays) => {
            if (sunrays > 50) println("Hiding in kennel!")
            else println("Going out to play.")
        }
        
       case Snow(snowflakes) => {
            if (snowflakes > 50) println("Total chaos!!!")
            else println("Sniffing around.")
       }
       
       case 12 => println("I'm a dog, i can't count!")
       case "hello" => println("Woof!")
    }
}
```

The only place where you deal with an actual `Actor` instance is *where you create code for it*.
Everywhere else, you are dealing with `ActorRef`s. It's very important, since `ActorRef`s are the cogs within Akka's machinery.

![Bigger picture](ActorRef.png)

The dispatcher serves as an `ExecutionContext`.

Pretty much everything in Akka happens within an `ExecutionContext`.
Using a scheduler also requires an execution context, which assigns the scheduled work to a thread:

```
println("Be there in a sec.")
context.system.scheduler.scheduleOnce(1 second) {
    println("I'm on some other thread.")
}
```

![Lifecycle](http://doc.akka.io/docs/akka/snapshot/_images/actor_lifecycle1.png)
