package org.euch.elevatorsim.simulation.winch

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.*

import java.time.Instant
import scala.concurrent.duration.*

object WinchActor {

  private case class Tick(override val now: Instant) extends WinchCommand

  // initial state
  def apply(winch: Winch): Behavior[WinchCommand] =
    idle(winch, WinchState.Stopped(SpeedAtTime(Instant.now(), 0)))

  private def idle(
      winch: Winch,
      state: WinchState.Stopped
  ): Behavior[WinchCommand] =
    Behaviors.receiveMessagePartial[WinchCommand] {
      case t: WinchCommand.MoveCommand =>
        log.info(s"${winch.name} idle -> speedUp")
        speedUp(winch, WinchState.SpeedUp(t.direction, SpeedAtTime(t.now, state.speed(t.now)), winch))
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
    }

  private def speedUp(
      winch: Winch,
      state: WinchState.SpeedUp
  ): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 100.millis)
      Behaviors.receiveMessagePartial[WinchCommand] {
        case Tick(now)
            if state.speed(now) >= winch.getNominalSpeed(state.direction) =>
          log.info(s"${winch.name} speedUp -> run")
          run(winch, WinchState.Run(state.direction, SpeedAtTime(now, state.speed(now)), winch))
        case WinchCommand.GetSpeed(now, replyTo) =>
          replyTo ! state.speed(now)
          Behaviors.unhandled
        case t: WinchCommand.MoveCommand =>
          log.info(s"${winch.name} speedUp -> 0 -> speedUp (direction change)")
          speedUp(winch, WinchState.SpeedUp(t.direction, SpeedAtTime(t.now, state.speed(t.now)), winch))
        case WinchCommand.Stop(now) =>
          log.info(s"${winch.name} speedUp -> slowDown")
          slowDown(winch, WinchState.SlowDown(state.direction, SpeedAtTime(now, state.speed(now)), winch))
      }
    }
  }

  private def run(
      winch: Winch,
      state: WinchState.Run
  ): Behavior[WinchCommand] = {
    Behaviors.receiveMessagePartial {
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
      case WinchCommand.Stop(now) =>
        log.info(s"${winch.name} run -> slowDown")
        slowDown(winch, WinchState.SlowDown(state.direction, SpeedAtTime(now, state.speed(now)), winch))
    }
  }

  private def slowDown(
      winch: Winch,
      state: WinchState.SlowDown
  ): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 100.millis)
      Behaviors.receiveMessagePartial {
        case Tick(now) if state.speed(now) >= 0 =>
          log.info(s"${winch.name} slowDown -> idle")
          idle(winch, WinchState.Stopped(SpeedAtTime(now, state.speed(now))))
        case t: WinchCommand.MoveCommand =>
          log.info(s"${winch.name} slowDown -> speedUp")
          speedUp(winch, WinchState.SpeedUp(t.direction, SpeedAtTime(t.now, state.speed(t.now)), winch))
        case WinchCommand.GetSpeed(now, replyTo) =>
          replyTo ! state.speed(now)
          Behaviors.unhandled
      }
    }
  }

}
