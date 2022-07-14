package org.euch.elevatorsim

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, DispatcherSelector, Terminated}
import org.euch.elevatorsim.Log.log
import org.euch.elevatorsim.domain.model.controls.{
  SimpleButtonPane,
  StickyButton
}
import org.euch.elevatorsim.domain.model.dimensions.{
  DimensionsBox,
  DimensionsRectangle
}
import org.euch.elevatorsim.domain.model.doors.TwoLeafDoor
import org.euch.elevatorsim.domain.model.portal.{Portal, SafePortalWithControls}
import org.euch.elevatorsim.domain.model.shaft.{BaseShaft, Shaft}
import org.euch.elevatorsim.domain.model.transport.Platform
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.*

import java.time.Instant

object Main {

  def apply(): Behavior[WinchCommand] = {
    Behaviors.setup { context =>
      log.info("Starting up")
//      val winch =
//        SingleSpeedWinch(1, 2, 50, "Winch", DimensionsRectangle(50, 50))
//      val winchActor =
//        context.spawn(
//          WinchActor(winch),
//          "winchActor"
//        )
//      context.watch(winchActor)
      Behaviors.receiveSignal { case (_, Terminated(_)) =>
        Behaviors.stopped
      }
    }
  }

  def main(args: Array[String]): Unit = {
    ActorSystem(Main(), "Elevatorsim")
  }

}
