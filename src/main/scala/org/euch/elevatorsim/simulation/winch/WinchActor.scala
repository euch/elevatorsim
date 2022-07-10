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
    idle(WinchState.Stopped(SpeedAtTime(Instant.now(), 0), winch))

  private def idle(state: WinchState.Stopped): Behavior[WinchCommand] =
    Behaviors.receiveMessagePartial[WinchCommand] {
      case t: WinchCommand.MoveCommand =>
        log.info(s"${state.winch.name} idle -> speedUp")
        speedUp(
          WinchState.SpeedUp(
            t.direction,
            SpeedAtTime(t.now, state.speed(t.now)),
            state.winch
          )
        )
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
    }

  private def speedUp(state: WinchState.SpeedUp): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 100.millis)
      Behaviors.receiveMessagePartial[WinchCommand] {
        case Tick(now)
            if state
              .speed(now) >= state.winch.getNominalSpeed(state.direction) =>
          log.info(s"${state.winch.name} speedUp -> run")
          run(
            WinchState.Run(
              state.direction,
              SpeedAtTime(now, state.speed(now)),
              state.winch
            )
          )
        case WinchCommand.GetSpeed(now, replyTo) =>
          replyTo ! state.speed(now)
          Behaviors.unhandled
        case t: WinchCommand.MoveCommand =>
          log.info(
            s"${state.winch.name} speedUp -> 0 -> speedUp (direction change)"
          )
          speedUp(
            WinchState.SpeedUp(
              t.direction,
              SpeedAtTime(t.now, state.speed(t.now)),
              state.winch
            )
          )
        case WinchCommand.Stop(now) =>
          log.info(s"${state.winch.name} speedUp -> slowDown")
          slowDown(
            WinchState.SlowDown(
              state.direction,
              SpeedAtTime(now, state.speed(now)),
              state.winch
            )
          )
      }
    }
  }

  private def run(state: WinchState.Run): Behavior[WinchCommand] = {
    Behaviors.receiveMessagePartial {
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
      case WinchCommand.Stop(now) =>
        log.info(s"${state.winch.name} run -> slowDown")
        slowDown(
          WinchState.SlowDown(
            state.direction,
            SpeedAtTime(now, state.speed(now)),
            state.winch
          )
        )
    }
  }

  private def slowDown(state: WinchState.SlowDown): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 100.millis)
      Behaviors.receiveMessagePartial {
        case Tick(now) if state.speed(now) >= 0 =>
          log.info(s"${state.winch.name} slowDown -> idle")
          idle(
            WinchState.Stopped(SpeedAtTime(now, state.speed(now)), state.winch)
          )
        case t: WinchCommand.MoveCommand =>
          log.info(s"${state.winch.name} slowDown -> speedUp")
          speedUp(
            WinchState.SpeedUp(
              t.direction,
              SpeedAtTime(t.now, state.speed(t.now)),
              state.winch
            )
          )
        case WinchCommand.GetSpeed(now, replyTo) =>
          replyTo ! state.speed(now)
          Behaviors.unhandled
      }
    }
  }

}
