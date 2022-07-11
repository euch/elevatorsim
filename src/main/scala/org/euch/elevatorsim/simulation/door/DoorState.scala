package org.euch.elevatorsim.simulation.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.doors.{Door, PoweredDoor}

import java.time.Instant
import scala.concurrent.duration.*

sealed trait DoorState {
  val t0percent: OpenPercentAtTime
  val door: Door
  def openPercent(now: Instant): Double
}

object DoorState {
  case class Closed(
      override val t0percent: OpenPercentAtTime,
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = 0
  }
  case class Opening(
      override val t0percent: OpenPercentAtTime,
      override val door: Door,
      closeTimeoutSeconds: Option[Long]
  ) extends DoorState {
    override def openPercent(now: Instant): Double = ???
  }

  case class Open(
                   override val t0percent: OpenPercentAtTime,
                   override val door: Door,
                   stayOpenSecondsOptional: Option[Long]
                 ) extends DoorState {
    override def openPercent(now: Instant): Double = 100
  }
  case class Closing(
      override val t0percent: OpenPercentAtTime,
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = ???
  }
  case class Stuck(
      override val t0percent: OpenPercentAtTime,
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = ???
  }
}

case class OpenPercentAtTime(instant: Instant, openPercent: Double)
