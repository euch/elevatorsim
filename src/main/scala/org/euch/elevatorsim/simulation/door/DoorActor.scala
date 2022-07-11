package org.euch.elevatorsim.simulation.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.Direction
import org.euch.elevatorsim.domain.model.doors.{Door, PoweredDoor}
import org.euch.elevatorsim.simulation.door

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{TimeUnit, *}

object DoorActor {

  private case class Tick(override val now: Instant) extends DoorCommand

//   initial state
  def apply(door: Door): Behavior[DoorCommand] =
    closed(DoorState.Closed(OpenPercentAtTime(Instant.now(), 0), door))

  private def closed(state: DoorState.Closed): Behavior[DoorCommand] =
    Behaviors.receiveMessagePartial[DoorCommand] {
      case DoorCommand.Open(now, closeTimeoutSeconds) =>
        log.info(s"${state.door.name} closed -> opening")
        opening(
          DoorState.Opening(
            OpenPercentAtTime(now, state.openPercent(now)),
            state.door,
            closeTimeoutSeconds
          )
        )
      case DoorCommand.GetOpenPercent(now, replyTo) =>
        replyTo ! state.openPercent(now)
        Behaviors.unhandled
    }

  private def opening(state: door.DoorState.Opening): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      Behaviors.receiveMessagePartial[DoorCommand] {
        case Tick(now) if state.openPercent(now) >= 100 =>
          log.info(s"${state.door.name} opening -> open")
          open(
            DoorState.Open(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door,
              state.closeTimeoutSeconds
            )
          )
        case DoorCommand.GetOpenPercent(now, replyTo) =>
          replyTo ! state.openPercent(now)
          Behaviors.unhandled
        case DoorCommand.Close(now) =>
          log.info(s"${state.door.name} opening -> closing")
          closing(
            DoorState.Closing(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door
            )
          )
      }
    }
  }

  private def open(state: door.DoorState.Open): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      Behaviors.receiveMessagePartial[DoorCommand] {
        case DoorCommand.GetOpenPercent(now, replyTo) =>
          replyTo ! state.openPercent(now)
          Behaviors.unhandled
        case DoorCommand.Open(now, closeTimeoutSeconds) =>
          log.info(s"${state.door.name} open -> open")
          opening(
            DoorState.Opening(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door,
              closeTimeoutSeconds
            )
          )
        case DoorCommand.Close(now) =>
          log.info(s"${state.door.name} open -> closing")
          closing(
            DoorState.Closing(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door
            )
          )
        case Tick(now) =>
          if (state.stayOpenSecondsOptional.nonEmpty) {
            val openTime =
              InstantUtils.diffSeconds(state.t0percent.instant, now)
            if (openTime >= state.stayOpenSecondsOptional.get) {
              log.info(s"${state.door.name} closing -> closing by timer")
              return closed(
                DoorState.Closed(
                  OpenPercentAtTime(now, state.openPercent(now)),
                  state.door
                )
              )
            }
          }
          Behaviors.unhandled
      }
    }
  }

  private def closing(state: door.DoorState.Closing): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startTimerAtFixedRate(Tick(Instant.now), 10.millis)
      Behaviors.receiveMessagePartial[DoorCommand] {
        case Tick(now) if state.openPercent(now) <= 0 =>
          log.info(s"${state.door.name} closing -> closed")
          closed(
            DoorState.Closed(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door
            )
          )
        case DoorCommand.Open(now, closeTimeoutSeconds) =>
          log.info(s"${state.door.name} closed -> opening")
          opening(
            DoorState.Opening(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door,
              closeTimeoutSeconds
            )
          )
        case DoorCommand.GetOpenPercent(now, replyTo) =>
          replyTo ! state.openPercent(now)
          Behaviors.unhandled
        case DoorCommand.Close(now) =>
          log.info(s"${state.door.name} opening -> closing")
          closing(
            DoorState.Closing(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door
            )
          )
      }
    }
  }

}