package org.euch.elevatorsim.simulation.actors.load

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.simulation.actors.door.OpenPercentAtTime
import org.euch.elevatorsim.simulation.actors.winch.WinchDirection

import java.time.Instant
import scala.concurrent.duration.*

sealed trait LoadState {
  val loadGroup: LoadGroup
  def loadPercent(now: Instant): Double
}

object LoadState {
  case class Waiting(
      override val loadGroup: LoadGroup
  ) extends LoadState {
    override def loadPercent(now: Instant): Double = 0
  }

  case class Loading(
      override val loadGroup: LoadGroup,
      t0percent: LoadPercentAtTime
  ) extends LoadState {
    override def loadPercent(now: Instant): Double = {
      val duration = InstantUtils.diffSeconds(now, t0percent.instant)
      val p = duration * loadGroup.loadSpeedPPS
      math.min(p, 100)
    }
    def loaded(now: Instant): Boolean = loadPercent(now) == 100
  }

  case class Traveling(override val loadGroup: LoadGroup) extends LoadState {
    override def loadPercent(now: Instant): Double = 100
  }

  case class Unloading(
      override val loadGroup: LoadGroup,
      t0percent: LoadPercentAtTime
  ) extends LoadState {
    override def loadPercent(now: Instant): Double = {
      val duration = InstantUtils.diffSeconds(now, t0percent.instant)
      val p = duration * loadGroup.unloadSpeedPPS
      math.max(0, p)
    }
    def unloaded(now: Instant): Boolean = loadPercent(now) == 0
  }

}

case class LoadPercentAtTime(instant: Instant, openPercent: Double)
