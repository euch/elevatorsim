package org.euch.elevatorsim.simulation.actors.door

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit, TestProbe}
import org.euch.elevatorsim.domain.model.dimensions.DimensionsRectangle
import org.euch.elevatorsim.domain.model.doors.TwoLeafDoor
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.actors.door.{DoorActor, DoorCommand}
import org.euch.elevatorsim.simulation.actors.winch.{WinchActor, WinchCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.*

class PoweredDoorActorFsmSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {

  "PoweredDoorActorFsmSpec" must {
    "work" in {
      // some overshooting expected
      val openRange = (-002, 100.002)

      val door =
        TwoLeafDoor(
          "Door",
          DimensionsRectangle(50, 50),
          50,
          openSpeedPPS = 50,
          closeSpeedPPS = 30
        )
      val doorActor = spawn(DoorActor(door))
      val probe = TestProbe[Double]()
      doorActor ! DoorCommand.GetOpenPercent(Instant.now(), probe.ref)
      probe.expectMessage(0) // initial open percentage = 0

      // ask to open
      doorActor ! DoorCommand.Open(Instant.now(), Option.empty)
      // wait
      waitForOpen(100)
      // should stay open
      doorActor ! DoorCommand.GetOpenPercent(Instant.now(), probe.ref)
      probe.expectMessage(100)

      doorActor ! DoorCommand.Close(Instant.now())
      waitForClose(50)
      doorActor ! DoorCommand.Open(Instant.now(), Option.empty)
      waitForOpen(70)
      doorActor ! DoorCommand.Close(Instant.now())
      waitForClose(0)
      doorActor ! DoorCommand.GetOpenPercent(Instant.now(), probe.ref)
      probe.expectMessage(0)

      @tailrec
      def waitForOpen(targetPercentage: Double): Unit = {
        val now = Instant.now()
        doorActor ! DoorCommand.GetOpenPercent(now, probe.ref)
        val percentage: Double = probe.receiveMessage(50.millis)
        assert(percentage >= openRange._1)
        assert(percentage <= openRange._2)
        if (percentage < targetPercentage) {
          // println(s"opening: $percentage -> $targetPercentage")
          waitForOpen(targetPercentage)
        } else {
          println(s"speed increased target reached: $percentage -> $targetPercentage")
        }
      }

      @tailrec
      def waitForClose(targetPercentage: Double): Unit = {
        val now = Instant.now()
        doorActor ! DoorCommand.GetOpenPercent(now, probe.ref)
        val percentage: Double = probe.receiveMessage(50.millis)
        assert(percentage >= openRange._1)
        assert(percentage <= openRange._2)
        if (percentage > targetPercentage) {
          // println(s"slowDown: $percentage -> $targetPercentage")
          waitForClose(targetPercentage)
        } else {
          println(s"speed reduced target reached: $percentage -> $targetPercentage")
        }
      }
    }
  }
}
