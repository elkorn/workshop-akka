package com.example

import ActorPerRequest.{WithProps, WithActorRef}
import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import com.example.api.ApiMessage
import com.example.api.protocols.JsonProtocol
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.Json4sSupport
import spray.json.DefaultJsonProtocol
import spray.routing.RequestContext

import scala.concurrent.duration._


trait ActorPerRequest extends Actor with Json4sSupport {

  import context._

  val json4sFormats = org.json4s.DefaultFormats

  case class Error(message: String)

  def r: RequestContext

  def target: ActorRef

  def message: ApiMessage

  setReceiveTimeout(2.seconds)
  target ! message

  def receive = {
    case res: ApiMessage => complete(StatusCodes.OK, res)
    case ReceiveTimeout => complete(StatusCodes.GatewayTimeout, Error("Request timeout"))
  }

  def complete[T <: AnyRef](
    status: StatusCode,
    obj: T) = {
    r.complete(status, obj)
    stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e => {
        complete(StatusCodes.InternalServerError, Error(e.getMessage))
        Stop
      }
    }
}

object ActorPerRequest {

  case class WithActorRef(
    r: RequestContext,
    target: ActorRef,
    message: ApiMessage) extends ActorPerRequest

  case class WithProps(
    r: RequestContext,
    props: Props,
    message: ApiMessage) extends ActorPerRequest {
    lazy val target = context.actorOf(props)
  }

}

trait ActorPerRequestCreator {
  this: Actor =>

  def actorPerRequest(
    r: RequestContext,
    target: ActorRef,
    message: ApiMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def actorPerRequest(
    r: RequestContext,
    props: Props,
    message: ApiMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}