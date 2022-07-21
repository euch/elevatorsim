package org.euch.elevatorsim.simulation.load

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.simulation.door.OpenPercentAtTime
import org.euch.elevatorsim.simulation.load.{LoadCommand, LoadState}

import java.time.Instant
import scala.concurrent.duration.*

class LoadActor {
  // initial state
  def apply(loadGroup: LoadGroup): Behavior[LoadCommand] =
    waiting(LoadState.Waiting(loadGroup))

  private def waiting(state: LoadState.Waiting): Behavior[LoadCommand] = {
    log.info(s"closed state: $state")
    Behaviors.receiveMessagePartial[LoadCommand] {
      case LoadCommand.Load(now) =>
        log.info(s"${state.loadGroup.name} waiting -> loading")
        loading(
          LoadState.Loading(
            state.loadGroup,
            LoadPercentAtTime(now, state.loadPercent(now))
          )
        )
      case LoadCommand.GetLoadPercent(now, replyTo) =>
        replyTo ! state.loadPercent(now)
        Behaviors.unhandled
    }
  }

  private def loading(state: LoadState.Loading): Behavior[LoadCommand] = {
    Behaviors.withTimers[LoadCommand] { timers =>
      timers.startSingleTimer(
        LoadCommand.Tick(Instant.now).asInstanceOf[LoadCommand],
        10.millis
      )
      log.info(s"opening state: $state")
      Behaviors.receiveMessagePartial[LoadCommand] {
        case LoadCommand.Tick(now) if state.loaded(now) =>
          log.info(s"${state.loadGroup.name} loading -> traveling")
          traveling(
            LoadState.Traveling(
              state.loadGroup
            )
          )
        case LoadCommand.GetLoadPercent(now, replyTo) =>
          replyTo ! state.loadPercent(now)
          Behaviors.unhandled
        case LoadCommand.Unload(now) =>
          log.info(s"${state.loadGroup.name} loading -> unloading")
          unloading(
            LoadState.Unloading(
              state.loadGroup,
              LoadPercentAtTime(now, state.loadPercent(now))
            )
          )
      }
    }
  }

  private def traveling(state: LoadState.Traveling): Behavior[LoadCommand] = {
    Behaviors.withTimers[LoadCommand] { timers =>
      timers.startSingleTimer(LoadCommand.Tick(Instant.now), 10.millis)
      log.info(s"open state: $state")
      Behaviors.receiveMessagePartial[LoadCommand] {
        case LoadCommand.GetLoadPercent(now, replyTo) =>
          replyTo ! state.loadPercent(now)
          Behaviors.unhandled
        case LoadCommand.Unload(now) =>
          log.info(s"${state.loadGroup.name} traveling -> unloading")
          unloading(
            LoadState.Unloading(
              state.loadGroup,
              LoadPercentAtTime(now, state.loadPercent(now))
            )
          )
      }
    }
  }

  private def unloading(state: LoadState.Unloading): Behavior[LoadCommand] = {
    Behaviors.withTimers[LoadCommand] { timers =>
      timers.startSingleTimer(LoadCommand.Tick(Instant.now), 10.millis)
      log.info(s"closing state: $state")
      Behaviors.receiveMessagePartial[LoadCommand] {
        case LoadCommand.Tick(now) if state.unloaded(now) =>
          log.info(s"${state.loadGroup.name} unloading -> [actor stop]")
          Behaviors.stopped
        case LoadCommand.Load(now) =>
          log.info(s"${state.loadGroup.name} unloading -> loading")
          loading(
            LoadState.Loading(
              state.loadGroup,
              LoadPercentAtTime(now, state.loadPercent(now))
            )
          )
        case LoadCommand.GetLoadPercent(now, replyTo) =>
          replyTo ! state.loadPercent(now)
          Behaviors.unhandled
      }
    }
  }
}
