package org.euch.elevatorsim.simulation.actors.load

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils
import org.euch.elevatorsim.InstantUtils.*
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.PercentUtils.{p0, p100, travelPercentageGTE0, travelPercentageLTE100}
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
    override def loadPercent(now: Instant): Double = p0
  }

  case class Loading(
      override val loadGroup: LoadGroup,
      t0percent: LoadPercentAtTime
  ) extends LoadState {
    override def loadPercent(now: Instant): Double = travelPercentageLTE100(
      diffSeconds(now, t0percent.instant),
      loadGroup.loadSpeedPPS
    )
    def loaded(now: Instant): Boolean = loadPercent(now) == p100
  }

  case class Traveling(override val loadGroup: LoadGroup) extends LoadState {
    override def loadPercent(now: Instant): Double = p100
  }

  case class Unloading(
      override val loadGroup: LoadGroup,
      t0percent: LoadPercentAtTime
  ) extends LoadState {
    override def loadPercent(now: Instant): Double = travelPercentageGTE0(
      diffSeconds(now, t0percent.instant),
      loadGroup.unloadSpeedPPS
    )
    def unloaded(now: Instant): Boolean = loadPercent(now) == p0
  }

}

case class LoadPercentAtTime(instant: Instant, openPercent: Double)
