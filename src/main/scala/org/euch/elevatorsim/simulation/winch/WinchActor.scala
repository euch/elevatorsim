package org.euch.elevatorsim.simulation.winch

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.*

import java.time.Instant
import scala.concurrent.duration.*

object WinchActor {

  private case class Tick(now: Instant) extends WinchCommand

  // initial state
  def apply(winch: Winch): Behavior[WinchCommand] =
    idle(WinchState.Stopped(winch))

  private def idle(state: WinchState.Stopped): Behavior[WinchCommand] = {
    log.info(s"idle state: $state")
    Behaviors.receiveMessagePartial[WinchCommand] {
      case t: WinchCommand.MoveCommand =>
        log.info(s"${state.winch.name} idle -> speedUp")
        speedUp(
          WinchState.Moving.NonLinearMoving.SpeedUp(
            t.direction,
            SpeedAtTime(t.now, 0),
            state.winch
          )
        )
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
    }
  }

  private def speedUp(
      state: WinchState.Moving.NonLinearMoving.SpeedUp
  ): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      log.info(s"speedup state: $state")
      Behaviors.receiveMessagePartial[WinchCommand] {
        case Tick(now)
            if state
              .speed(now) >= state.winch.getNominalSpeed(state.direction) =>
          log.info(s"${state.winch.name} speedUp -> run")
          run(
            WinchState.Moving.LinearMoving.Run(
              state.direction,
              state.winch
            )
          )
        case WinchCommand.GetSpeed(now, replyTo) =>
          replyTo ! state.speed(now)
          Behaviors.unhandled
        case t: WinchCommand.MoveCommand =>
          log.info(s"${state.winch.name} speedUp -> 0 -> speedUp")
          speedUp(
            WinchState.Moving.NonLinearMoving.SpeedUp(
              t.direction,
              SpeedAtTime(t.now, state.speed(t.now)),
              state.winch
            )
          )
        case WinchCommand.Stop(now) =>
          log.info(s"${state.winch.name} speedUp -> slowDown")
          slowDown(
            WinchState.Moving.NonLinearMoving.SlowDown(
              state.direction,
              SpeedAtTime(now, state.speed(now)),
              state.winch
            )
          )
      }
    }
  }

  private def run(
      state: WinchState.Moving.LinearMoving.Run
  ): Behavior[WinchCommand] = {
    log.info(s"run state: $state")
    Behaviors.receiveMessagePartial {
      case WinchCommand.GetSpeed(now, replyTo) =>
        replyTo ! state.speed(now)
        Behaviors.unhandled
      case WinchCommand.Stop(now) =>
        log.info(s"${state.winch.name} run -> slowDown")
        slowDown(
          WinchState.Moving.NonLinearMoving.SlowDown(
            state.direction,
            SpeedAtTime(now, state.speed(now)),
            state.winch
          )
        )
    }
  }

  private def slowDown(
      state: WinchState.Moving.NonLinearMoving.SlowDown
  ): Behavior[WinchCommand] = {
    Behaviors.withTimers[WinchCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      log.info(s"slowdown state: $state")
      Behaviors.receiveMessagePartial {
        case Tick(now) if state.speed(now) >= 0 =>
          log.info(s"${state.winch.name} slowDown -> idle")
          idle(
            WinchState.Stopped(state.winch)
          )
        case t: WinchCommand.MoveCommand =>
          log.info(s"${state.winch.name} slowDown -> speedUp")
          speedUp(
            WinchState.Moving.NonLinearMoving.SpeedUp(
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
