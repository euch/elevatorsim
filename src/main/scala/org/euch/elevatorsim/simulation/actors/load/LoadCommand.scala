package org.euch.elevatorsim.simulation.actors.load

import akka.actor.typed.ActorRef

import java.time.Instant

trait LoadCommand {
  val now: Instant
}
object LoadCommand {
  case class Load(override val now: Instant) extends LoadCommand
  case class Unload(override val now: Instant) extends LoadCommand
  case class GetLoadPercent(
      override val now: Instant,
      replyTo: ActorRef[Double]
  ) extends LoadCommand
  case class Tick(override val now: Instant) extends LoadCommand

}
