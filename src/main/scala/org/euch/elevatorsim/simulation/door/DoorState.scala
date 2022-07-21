package org.euch.elevatorsim.simulation.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.doors.{Door, PoweredDoor}
import org.euch.elevatorsim.simulation.winch.WinchDirection

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
    override def openPercent(now: Instant): Double = 0

  }

  case class Opening(
      t0percent: OpenPercentAtTime,
      override val door: Door,
      closeTimeoutSeconds: Option[Long]
  ) extends DoorState {
    override def openPercent(now: Instant): Double = door match
      case poweredDoor: PoweredDoor =>
        val duration = InstantUtils.diffSeconds(now, t0percent.instant)
        val p = duration * poweredDoor.openSpeedPPS
        math.min(p, 100)
      case _ => 100
    def fullyOpened(now: Instant): Boolean = openPercent(now) == 100
  }

  case class Open(
      opened: Instant,
      override val door: Door,
      stayOpenSecondsOptional: Option[Long]
  ) extends DoorState {
    override def openPercent(now: Instant): Double = 100
  }

  case class Closing(
      t0percent: OpenPercentAtTime,
      override val door: Door
  ) extends DoorState {
    override def openPercent(now: Instant): Double = door match
      case poweredDoor: PoweredDoor =>
        val duration = InstantUtils.diffSeconds(now, t0percent.instant)
        val p = 100 - duration * poweredDoor.closeSpeedPPS
        math.max(0, p)
      case _ => 0
    def fullyClosed(now: Instant): Boolean = openPercent(now) == 0
  }
}

case class OpenPercentAtTime(instant: Instant, openPercent: Double)
