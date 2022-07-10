package org.euch.elevatorsim.simulation.winch

import akka.actor.typed.ActorRef
import org.euch.elevatorsim.domain.model.Direction

import java.time.Instant

trait WinchCommand {
  val now: Instant
}
object WinchCommand {
  sealed trait MoveCommand extends WinchCommand {
    val direction: Direction
  }
  object MoveCommand {
    case class GoUp(override val now: Instant) extends MoveCommand {
      override val direction: Direction = Direction.Up
    }
    case class GoDown(override val now: Instant) extends MoveCommand {
      override val direction: Direction = Direction.Down
    }
  }
  case class Stop(override val now: Instant) extends WinchCommand
  case class GetSpeed(override val now: Instant, replyTo: ActorRef[Double])
      extends WinchCommand

}
