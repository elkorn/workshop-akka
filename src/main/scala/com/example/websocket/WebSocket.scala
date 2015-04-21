package com.example.websocket

import java.util.regex.Pattern

import akka.actor.ActorRef

object WebSocket {
  final case class Push(msg: String)

  final case class Pull(
    path: String,
    msg: String)

  final case class Open(
    path: String,
    origin: ActorRef)

  type Route[T] = PartialFunction[String, T]

  case class RouteMatcher[T](pattern: String, value: T) extends Route[T] {
    val regex: Pattern = Pattern.compile(pattern.replace("*", ".*?"))

    def apply(path: String): T = applyOrElse(path, { p: String => throw new Exception })

    def isDefinedAt(path: String): Boolean = regex.matcher(path).matches()

    override def applyOrElse[A1 <: String, B1 >: T](x: A1, default: A1 => B1): B1 =
      if (isDefinedAt(x)) value else default(x)
  }

  object Routes {
    def apply[T](routes: (String, T)*): Route[T] = {
      val rm: Seq[RouteMatcher[T]] = routes.map({
        case (path, actor) => RouteMatcher(path, actor)
      })

      rm.reduce[Route[T]](_ orElse _)
    }
  }
}

trait WebSocket {
 def send(message: String): Unit

 def close(): Unit

 def path(): String
}
