package org.euch.elevatorsim.simulation.winch

import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.winch.{SingleSpeedWinch, Winch}

import java.time.Instant

protected sealed trait WinchState {
  def speed(now: Instant): Double
}
protected object WinchState {
  case object Stopped extends WinchState {
    override def speed(now: Instant): Double = 0
  }
  case class SpeedUp(
      direction: Direction,
      since: Instant,
      private val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      case ssw: SingleSpeedWinch => ssw.getNominalSpeed(direction)
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
  case class Run(
      direction: Direction,
      since: Instant,
      private val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      case ssw: SingleSpeedWinch => ssw.getNominalSpeed(direction)
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
  case class SlowDown(
      direction: Direction,
      since: Instant,
      private val winch: Winch
  ) extends WinchState {
    override def speed(now: Instant): Double = winch match {
      case _: SingleSpeedWinch => 0
      case _ => throw new java.lang.Exception("winch type not supported")
    }
  }
}
