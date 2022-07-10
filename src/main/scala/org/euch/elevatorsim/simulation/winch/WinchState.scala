package org.euch.elevatorsim.simulation.winch

import org.euch.elevatorsim.InstantUtils.diffSeconds
import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.winch.{
  SingleSpeedWinch,
  VariableSpeedWinch,
  Winch
}

import java.time.Instant

protected sealed trait WinchState {
  val t0v0: SpeedAtTime
  protected val winch: Winch
  def speed(now: Instant): Double
}
protected object WinchState {
  case class Stopped(override val t0v0: SpeedAtTime, override val winch: Winch)
      extends WinchState {
    override def speed(now: Instant): Double = 0
  }
  case class SpeedUp(
      direction: Direction,
      override val t0v0: SpeedAtTime,
      override val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      // accelerates immediately
      case w: SingleSpeedWinch =>
        w.getNominalSpeed(direction)
      // v = v0 + a * t
      case w: VariableSpeedWinch =>
        val t = diffSeconds(now, t0v0.instant)
        t0v0.speed + w.speedUpAccelerations.get(direction) * t
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
  case class Run(
      direction: Direction,
      override val t0v0: SpeedAtTime,
      override val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      case w: SingleSpeedWinch   => w.getNominalSpeed(direction)
      case w: VariableSpeedWinch => w.getNominalSpeed(direction)
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
  case class SlowDown(
      direction: Direction,
      override val t0v0: SpeedAtTime,
      override val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      // decelerates immediately
      case _: SingleSpeedWinch => 0
      // v = v0 + a * t
      case w: VariableSpeedWinch =>
        val t = diffSeconds(now, t0v0.instant)
        t0v0.speed + w.slowDownAccelerations.get(direction) * t
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
}

case class SpeedAtTime(instant: Instant, speed: Double)
