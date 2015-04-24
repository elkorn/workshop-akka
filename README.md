# Supervision

Every actor is a child of some other actor.
![Hierarchy](http://doc.akka.io/docs/akka/snapshot/_images/guardians.png)

## User guardian
This is the parent (direct or indirect) for all actors created within an `ActorSystem` by using `system.actorOf`.

Its path is denoted as `/user`.

When this actor terminates, all underlying actors will be *shut down*.

## System guardian
This actor serves to have logging active during the shutdown of the `/user` part of the system.
The system guardian watches the user guardian and initiates its own shutdown upon receiving a `Terminated` message.

Its path is denoted as `/system`.

## Root guardian
The root guardian supervises both the user and system guardians as well as other actors:

- `/deadLetters` - an actor receiving all messages that are sent to stopped or non-existing actors.
- `/temp` - for all short-lived actors created on-the-fly (but not by the user), e.g. those used in the implementation of `Future`s.
- `/remote` - an artificial endpoint which allows access to remote actors.


## Lifecycle and supervision strategies
![Lifecycle](http://doc.akka.io/docs/akka/snapshot/_images/actor_lifecycle1.png)

You can override lifecycle methods in your actors to plug in some behaviors.

The most important thing here however are the supervision strategies.

When an actor *shuts down*, it actually means that it moves forward in its lifecycle:

1. Suspends the actor (which means that it will not process normal messages until resumed), and recursively suspend all children.
2. Calls the old instance’s preRestart hook (defaults to sending termination requests to all children and calling `postStop`).
3. Waits for all children which were requested to terminate (using `context.stop()`) during `preRestart` to actually terminate; this is non-blocking, the termination notice from the last killed child will effect the progression to the next step.

After that, its further existence is depending on how its supervisor is configured to behave.

```
override val supervisorStrategy =
  OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: ArithmeticException      => Resume
    case _: NullPointerException     => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception                => Escalate
  }
```

When restarting, an actor does the following:
4. Creates new actor instance by invoking the originally provided factory again.
5. Invokes `postRestart` on the new instance (which by default also calls `preStart`).
6. Sends a restart request to all children which were not killed in step 3; restarted children will follow the same process recursively, from step 2.
7. Resumes itself.

Please note `OneForOneStrategy` - it means that when an actor shuts down for some reason, the supervisor will try to apply the strategy to that actor only.
An `AllForOneStrategy` will cause it to apply the strategy to all of its children if one of them shuts down.

Use case for `AllForOneStrategy`: processes with inextricably linked steps, where a failure of one step should 'transactionally' abort the whole process.

## Killing an actor

`context.stop(actor: ActorRef)` and `actor ! PosionPill` terminate the actor and stop the message queue. They will cause the actor to cease processing messages, send a stop call to all its children, wait for them to terminate, then call its `postStop` hook. All further messages are sent to the dead letters mailbox.
The difference is in which messages get processed *before* this sequence starts.
In the case of the `stop` call, the message currently being processed is completed first, with all others discarded.
When sending a `PoisonPill`, this is simply another message in the queue, so the sequence will start when the `PoisonPill` is received.

Using `actor ! Kill` causes the actor to throw an `ActorKilledException` which gets handled using the normal supervisor mechanism.
So the behaviour here depends on what you've defined in your supervisor strategy.
The mailbox persists, so when the actor restarts it will still have the old messages except for the one that caused the failure.

### The case of a router
PoisonPill messages sent to the router will not be sent on to routees.

However, it will stop the router and when the router stops it also stops its children.
Each child will process its current message and then stop.
Instead you should wrap a PoisonPill message inside a Broadcast message so that each routee will receive the PoisonPill message.
To first process all the messages currently in their mailboxes:

`router ! Broadcast(PoisonPill)`

Note that this will stop all routees, even if the routees aren't children of the router.
Each routee will receive a PoisonPill message.
