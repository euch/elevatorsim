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

  private def opening(state: door.DoorState.Opening): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      log.info(s"opening state: $state")
      Behaviors.receiveMessagePartial[DoorCommand] {
        case Tick(now) if state.openPercent(now) >= 100 =>
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

  private def open(state: door.DoorState.Open): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
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
        case Tick(now) =>
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

  private def closing(state: door.DoorState.Closing): Behavior[DoorCommand] = {
    Behaviors.withTimers[DoorCommand] { timers =>
      timers.startSingleTimer(Tick(Instant.now), 10.millis)
      log.info(s"closing state: $state")
      Behaviors.receiveMessagePartial[DoorCommand] {
        case Tick(now) if state.openPercent(now) <= 0 =>
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
