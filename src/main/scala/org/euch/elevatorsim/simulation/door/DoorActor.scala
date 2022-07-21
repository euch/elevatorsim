package org.euch.elevatorsim.simulation.door

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.euch.elevatorsim.InstantUtils
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.doors.{Door, PoweredDoor}
import org.euch.elevatorsim.simulation.winch.WinchDirection

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.*

object DoorActor {

  // initial state
  def apply(door: Door): Behavior[DoorCommand] =
    closed(DoorState.Closed(door))

  private def closed(state: DoorState.Closed): Behavior[DoorCommand] = {
    log.info(s"closed state: $state")
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
  }

  private def opening(state: DoorState.Opening): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(DoorCommand.Tick(Instant.now), 10.millis)
      log.info(s"opening state: $state")
      Behaviors.receiveMessagePartial[DoorCommand] {
        case DoorCommand.Tick(now) if state.fullyOpened(now) =>
          log.info(s"${state.door.name} opening -> open")
          open(
            DoorState.Open(
              now,
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

  private def open(state: DoorState.Open): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(DoorCommand.Tick(Instant.now), 10.millis)
      log.info(s"open state: $state")
      Behaviors.receiveMessagePartial[DoorCommand] {
        case DoorCommand.GetOpenPercent(now, replyTo) =>
          replyTo ! state.openPercent(now)
          Behaviors.unhandled
        case DoorCommand.Close(now) =>
          log.info(s"${state.door.name} open -> closing")
          closing(
            DoorState.Closing(
              OpenPercentAtTime(now, state.openPercent(now)),
              state.door
            )
          )
        case DoorCommand.Tick(now) =>
          if (state.stayOpenSecondsOptional.nonEmpty) {
            val openTime =
              InstantUtils.diffSeconds(state.opened, now)
            if (openTime >= state.stayOpenSecondsOptional.get) {
              log.info(s"${state.door.name} closing -> closing by timer")
              return closed(
                DoorState.Closed(
                  state.door
                )
              )
            }
          }
          Behaviors.unhandled
      }
    }
  }

  private def closing(state: DoorState.Closing): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(DoorCommand.Tick(Instant.now), 10.millis)
      log.info(s"closing state: $state")
      Behaviors.receiveMessagePartial[DoorCommand] {
        case DoorCommand.Tick(now) if state.fullyClosed(now) =>
          log.info(s"${state.door.name} closing -> closed")
          closed(
            DoorState.Closed(
              state.door
            )
          )
        case DoorCommand.Open(now, closeTimeoutSeconds) =>
          log.info(s"${state.door.name} closing -> opening")
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
    }
  }

}
