package org.euch.elevatorsim.simulation.winch

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit, TestProbe}
import org.euch.elevatorsim.domain.model.dimensions.DimensionsRectangle
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.{WinchActor, WinchCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import java.time.Instant

class SingleSpeedWinchFsmSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {

  "SingleSpeedWinchFsmSpec" must {
    "work" in {
      val winch =
        SingleSpeedWinch(1, 2, 50, "Winch", DimensionsRectangle(50, 50))
      val winchActor = spawn(WinchActor(winch))
      val probe = TestProbe[Double]()
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(0) // initial speed = 0

      { // This type of winch gains speed immediately
        val now = Instant.now()
        winchActor ! WinchCommand.MoveCommand.GoUp(now)
        winchActor ! WinchCommand.GetSpeed(now, probe.ref)
        probe.expectMessage(1)
      }

      { // This type of winch slows down immediately
        val now = Instant.now()
        winchActor ! WinchCommand.Stop(now)
        winchActor ! WinchCommand.GetSpeed(now, probe.ref)
        probe.expectMessage(0)
      }

      { // This type of winch is reversible without 'Stop' command
        val now = Instant.now()
        winchActor ! WinchCommand.MoveCommand.GoDown(now)
        winchActor ! WinchCommand.MoveCommand.GoUp(now)
        winchActor ! WinchCommand.GetSpeed(now, probe.ref)
        probe.expectMessage(1)
      }

      { // Check reverse with 'Stop' command
        val now = Instant.now()
        winchActor ! WinchCommand.MoveCommand.GoDown(now)
        winchActor ! WinchCommand.Stop(now)
        winchActor ! WinchCommand.MoveCommand.GoUp(now)
        winchActor ! WinchCommand.GetSpeed(now, probe.ref)
        probe.expectMessage(1)
      }
    }
  }
}
