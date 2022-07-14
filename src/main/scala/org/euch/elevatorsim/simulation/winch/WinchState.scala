package org.euch.elevatorsim.simulation.winch

import org.euch.elevatorsim.InstantUtils.diffSeconds
import org.euch.elevatorsim.domain.model.winch.{SingleSpeedWinch, VariableSpeedWinch, Winch}

import java.time.Instant

protected sealed trait WinchState {
  val winch: Winch
  def speed(now: Instant): Double
}

protected object WinchState {
  case class Stopped(override val winch: Winch) extends WinchState {
    override def speed(now: Instant): Double = 0
  }

  trait Moving extends WinchState {
    val direction: WinchDirection
  }

  object Moving {
    object LinearMoving {
      case class Run(
                      override val direction: WinchDirection,
                      override val winch: Winch
                    ) extends WinchState.Moving {
        override def speed(now: Instant): Double =
          winch.getNominalSpeed(direction)
      }
    }

    trait NonLinearMoving extends Moving {
      val t0v0: SpeedAtTime
    }

    object NonLinearMoving {
      case class SpeedUp(
                          override val direction: WinchDirection,
                          override val t0v0: SpeedAtTime,
                          override val winch: Winch
                        ) extends NonLinearMoving {
        override def speed(now: Instant): Double = {
          winch match {
            // accelerates immediately
            case w: SingleSpeedWinch =>
              w.getNominalSpeed(direction)
            // v = v0 + a * t
            case w: VariableSpeedWinch =>
              val t = diffSeconds(now, t0v0.instant)
              val a = direction match {
                case WinchDirection.Up => w.speedUpAccelerations.upwards
                case WinchDirection.Down => w.speedUpAccelerations.downwards
              }
              t0v0.speed + a * t
            case _ =>
              throw new java.lang.Exception("winch type not supported")
          }
        }
      }

      case class SlowDown(
                           override val direction: WinchDirection,
                           override val t0v0: SpeedAtTime,
                           override val winch: Winch
                         ) extends WinchState.Moving.NonLinearMoving {
        override def speed(now: Instant): Double = {
          winch match {
            // decelerates immediately
            case _: SingleSpeedWinch => 0
            // v = v0 + a * t
            case w: VariableSpeedWinch =>
              val t = diffSeconds(now, t0v0.instant)
              val a = direction match {
                case WinchDirection.Up => w.slowDownAccelerations.upwards
                case WinchDirection.Down => w.slowDownAccelerations.downwards
              }
              val speed = t0v0.speed + a * t
              direction match {
                case WinchDirection.Up => math.max(0, speed)
                case WinchDirection.Down => math.min(0, speed)
              }
            case _ =>
              throw new java.lang.Exception("winch type not supported")
          }
        }
      }
    }
  }
}

case class SpeedAtTime(instant: Instant, speed: Double)
