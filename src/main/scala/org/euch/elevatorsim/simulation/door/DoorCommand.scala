package org.euch.elevatorsim.simulation.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.*

import java.time.Instant
import scala.concurrent.duration.*

trait DoorCommand {
  val now: Instant
}
object DoorCommand {
  case class Open(override val now: Instant, closeTimeoutSeconds: Option[Long]) extends DoorCommand
  case class Close(override val now: Instant) extends DoorCommand
  case class GetOpenPercent(override val now: Instant, replyTo: ActorRef[Double]) extends DoorCommand
}
