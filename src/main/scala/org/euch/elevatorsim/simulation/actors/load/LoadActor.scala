package org.euch.elevatorsim.simulation.actors.load

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.loads.LoadGroup
import org.euch.elevatorsim.simulation.actors.door.OpenPercentAtTime

import java.time.Instant
import scala.concurrent.duration.*

object LoadActor {
  // initial state
  def apply(loadGroup: LoadGroup): Behavior[LoadCommand] =
    waiting(LoadState.Waiting(loadGroup))

  private def waiting(state: LoadState.Waiting): Behavior[LoadCommand] = {
    log.info(s"waiting state: $state")
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
      timers.startSingleTimer(LoadCommand.Tick(Instant.now), 10.millis)
      log.info(s"loading state: $state")
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
          if (state.loaded(now)) {
            traveling(
              LoadState.Traveling(
                state.loadGroup
              )
            )
          } else {
            Behaviors.unhandled
          }
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
    log.info(s"traveling state: $state")
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

  private def unloading(state: LoadState.Unloading): Behavior[LoadCommand] = {
    Behaviors.withTimers[LoadCommand] { timers =>
      timers.startSingleTimer(LoadCommand.Tick(Instant.now), 10.millis)
      log.info(s"unloading state: $state")
      Behaviors.receiveMessagePartial[LoadCommand] {
        case LoadCommand.Tick(now) if state.unloaded(now) =>
          log.info(s"${state.loadGroup.name} unloading -> [actor stop 0]")
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
          if (state.unloaded(now)) {
            log.info(s"${state.loadGroup.name} unloading -> [actor stop 1]")
            Behaviors.stopped
          } else {
            Behaviors.unhandled

          }
      }
    }
  }
}
