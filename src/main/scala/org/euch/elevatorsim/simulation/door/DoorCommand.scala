package org.euch.elevatorsim.simulation.door

import akka.actor.typed.ActorRef

import java.time.Instant

trait DoorCommand {
  val now: Instant
}
object DoorCommand {
  case class Open(override val now: Instant, closeTimeoutSeconds: Option[Long])
      extends DoorCommand
  case class Close(override val now: Instant) extends DoorCommand
  case class GetOpenPercent(
      override val now: Instant,
      replyTo: ActorRef[Double]
  ) extends DoorCommand
  case class Tick(override val now: Instant) extends DoorCommand
}
