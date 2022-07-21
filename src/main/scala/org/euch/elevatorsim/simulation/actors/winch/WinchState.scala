package org.euch.elevatorsim.simulation.actors.winch

import org.euch.elevatorsim.InstantUtils.*
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.PercentUtils.*
import org.euch.elevatorsim.domain.model.winch.{SingleSpeedWinch, VariableSpeedWinch, Winch}

import java.time.Instant
import scala.math.{max, min}

protected sealed trait WinchState {
  val winch: Winch

  def speed(now: Instant): Double
}

protected object WinchState {
  case class Stopped(override val winch: Winch) extends WinchState {
    override def speed(now: Instant): Double = p0
  }

  trait Moving extends WinchState {
    val direction: WinchDirection

    protected def nominalSpeed: Double = direction match {
      case WinchDirection.Up   => winch.nominalSpeedUp
      case WinchDirection.Down => winch.nominalSpeedDown
    }
  }

  object Moving {
    object LinearMoving {
      case class Run(
          override val direction: WinchDirection,
          override val winch: Winch
      ) extends WinchState.Moving {
        override def speed(now: Instant): Double = super.nominalSpeed
      }
    }

    trait NonLinearMoving extends Moving {
      val t0v0: SpeedAtTime

      def targetSpeedReached(now: Instant): Boolean
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
            case _: SingleSpeedWinch => super.nominalSpeed
            case w: VariableSpeedWinch =>
              val durationSeconds = diffSeconds(now, t0v0.instant)
              direction match {
                case WinchDirection.Up =>
                  min(
                    w.nominalSpeedUp,
                    t0v0.speed + w.speedUpAccelerations.upwards * durationSeconds
                  )
                case WinchDirection.Down =>
                  max(
                    w.nominalSpeedDown,
                    t0v0.speed + w.speedUpAccelerations.downwards * durationSeconds
                  )
              }
            case _ =>
              throw new java.lang.Exception("winch type not supported")
          }
        }

        override def targetSpeedReached(now: Instant): Boolean =
          speed(now) == super.nominalSpeed
      }

      case class SlowDown(
          override val direction: WinchDirection,
          override val t0v0: SpeedAtTime,
          override val winch: Winch
      ) extends WinchState.Moving.NonLinearMoving {
        override def speed(now: Instant): Double = {
          winch match {
            // decelerates immediately
            case _: SingleSpeedWinch => p0
            case w: VariableSpeedWinch =>
              val t = diffSeconds(now, t0v0.instant)
              val speed = direction match {
                case WinchDirection.Up =>
                  max(p0, t0v0.speed + w.slowDownAccelerations.upwards * t)
                case WinchDirection.Down =>
                  min(p0, t0v0.speed + w.slowDownAccelerations.downwards * t)
              }
              log.info(s"slowdown speed = $speed")
              speed
            case _ =>
              throw new java.lang.Exception("winch type not supported")
          }
        }

        override def targetSpeedReached(now: Instant): Boolean =
          speed(now) == p0
      }
    }
  }
}

case class SpeedAtTime(instant: Instant, speed: Double)
