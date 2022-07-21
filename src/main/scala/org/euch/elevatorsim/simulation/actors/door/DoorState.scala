package org.euch.elevatorsim.simulation.actors.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils.*
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.PercentUtils.*
import org.euch.elevatorsim.domain.model.doors.{Door, PoweredDoor}
import org.euch.elevatorsim.simulation.actors.winch.WinchDirection

import java.time.Instant
import scala.concurrent.duration.*

sealed trait DoorState {
  val door: Door
  def openPercent(now: Instant): Double
}

object DoorState {

  case class Closed(
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = p0

  }

  case class Opening(
      t0percent: OpenPercentAtTime,
      override val door: Door,
      closeTimeoutSeconds: Option[Long]
  ) extends DoorState {
    override def openPercent(now: Instant): Double = door match
      case poweredDoor: PoweredDoor =>
        travelPercentageLTE100(
          diffSeconds(now, t0percent.instant),
          poweredDoor.openSpeedPPS
        )
      case _ => p100
    def fullyOpened(now: Instant): Boolean = openPercent(now) == p100
  }

  case class Open(
      opened: Instant,
      override val door: Door,
      stayOpenSecondsOptional: Option[Long]
  ) extends DoorState {
    override def openPercent(now: Instant): Double = p100
  }

  case class Closing(
      t0percent: OpenPercentAtTime,
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = door match
      case poweredDoor: PoweredDoor =>
        travelPercentageGTE0(
          diffSeconds(now, t0percent.instant),
          poweredDoor.closeSpeedPPS
        )
      case _ => p0
    def fullyClosed(now: Instant): Boolean = openPercent(now) == p0
  }
}

case class OpenPercentAtTime(instant: Instant, openPercent: Double)
