package org.euch.elevatorsim.simulation.winch

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit, TestProbe}
import org.euch.elevatorsim.domain.model.dimensions.DimensionsRectangle
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.winch.{WinchActor, WinchCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.math.abs

class VariableSpeedWinchFsmSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {

  "VariableSpeedWinchFsmSpec" must {
    "work" in {
      val speedUpAccelerations =
        VerticalAccelerations(upwards = 2, downwards = -2)
      val slowDownAccelerations =
        VerticalAccelerations(upwards = -2, downwards = 2)
      val runawaySpeed = 3
      val winch =
        VariableSpeedWinch(
          nominalSpeedUp = 1,
          nominalSpeedDown = -1,
          weight = 50,
          name = "Winch",
          dimensions = DimensionsRectangle(50, 50),
          speedUpAccelerations = speedUpAccelerations,
          slowDownAccelerations = slowDownAccelerations
        )
      val winchActor = spawn(WinchActor(winch))
      val probe = TestProbe[Double]()
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(0) // initial speed = 0

      // 1. Start, wait for nominal, stop, wait for full stop
      {
        val now = Instant.now()
        winchActor ! WinchCommand.MoveCommand.GoUp(now)
        winchActor ! WinchCommand.GetSpeed(now, probe.ref)
      }
      // atm of command receive it's still 0
      probe.expectMessage(0)
      // and then increases up to nominal
      waitForSpeedUp(
        "1 - reach nominal speed",
        winch.nominalSpeedUp
      )
      // and stays nominal
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(winch.nominalSpeedUp)
      // start slowing down
      winchActor ! WinchCommand.Stop(Instant.now())
      waitForSlowDown("1 - reach zero speed", 0)
      // should stay stopped
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(0)

      // 2. Change direction while speeding up
      winchActor ! WinchCommand.MoveCommand.GoUp(Instant.now())
      // speed increases, wait for less than nominal
      waitForSpeedUp("2 - reach specific speed", 0.003)
      // ask to change direction
      winchActor ! WinchCommand.MoveCommand.GoDown(Instant.now())
      // nothing happens because Stop command required to change direction
      waitForSlowDown("2 - reach specific speed", 0.2)
      // ask to stop
      winchActor ! WinchCommand.Stop(Instant.now())
      // wait for zero speed
      waitForSlowDown("2 - reach zero speed", 0)
      // ask to change direction once again
      winchActor ! WinchCommand.MoveCommand.GoDown(Instant.now())
      // wait for negative nominal speed
      waitForSpeedUp("2 - reach nominal speed", -1)
      // should keep running down
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(-1)

      // 3. Change direction while slowing down
      winchActor ! WinchCommand.Stop(Instant.now())
      // change direction before full stop
      waitForSlowDown("3 - reach specific speed", -0.3)
      winchActor ! WinchCommand.MoveCommand.GoUp(Instant.now())
      // slow down again
      waitForSpeedUp("3 - reach specific speed", 0.5)
      winchActor ! WinchCommand.Stop(Instant.now())
      // and before in stops, ask it to gain speed again in the same direaction
      waitForSlowDown("3 - reach specific speed", 0.3)
      winchActor ! WinchCommand.MoveCommand.GoUp(Instant.now())
      // let it reach nominal
      waitForSpeedUp("3 - reach nominal speed", 1)
      // should keep nominal
      winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
      probe.expectMessage(1)

      @tailrec
      def waitForSpeedUp(descr: String, targetSpeed: Double): Unit = {
        winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
        val speed: Double = probe.receiveMessage(50.millis)
        assert(abs(speed) < runawaySpeed)
        if (abs(speed) < abs(targetSpeed)) {
          println(s"speedUp ($descr): $speed -> $targetSpeed")
          waitForSpeedUp(descr, targetSpeed)
        } else {
          println(s"speed increased target reached: $speed -> $targetSpeed")
        }
      }

      @tailrec
      def waitForSlowDown(descr: String, targetSpeed: Double): Unit = {
        winchActor ! WinchCommand.GetSpeed(Instant.now(), probe.ref)
        val speed: Double = probe.receiveMessage(50.millis)
        assert(abs(speed) < runawaySpeed)
        if (abs(speed) > abs(targetSpeed)) {
          println(s"slowDown ($descr): $speed -> $targetSpeed")
          waitForSlowDown(descr, targetSpeed)
        } else {
          println(s"speed reduced target reached: $speed -> $targetSpeed")
        }
      }
    }
  }
}
