package org.euch.elevatorsim.simulation.actors.load

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit, TestProbe}
import org.euch.elevatorsim.domain.model.dimensions.{DimensionsBox, DimensionsRectangle}
import org.euch.elevatorsim.domain.model.loads.{LoadGroup, Passenger}
import org.euch.elevatorsim.simulation.actors.load.{LoadActor, LoadCommand}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.*

class LoadActorFsmSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "LoadActorFsmSpec" must {
    "work" in {

      // no overshooting expected
      val openRange = (0, 100)

      val loadGroup = LoadGroup(
        "LoadGroup",
        1,
        List(
          Passenger(
            name = "James",
            dimensions = DimensionsBox(40, 60, 180),
            weight = 100
          )
        ),
        50,
        50
      )
      val loadActor = spawn(LoadActor(loadGroup))
      val probe = TestProbe[Double]()

      loadActor ! LoadCommand.GetLoadPercent(Instant.now(), probe.ref)
      probe.expectMessage(0) // initial load percentage = 0

      loadActor ! LoadCommand.Load(Instant.now())
      waitForOpen(100) // should be loaded
      loadActor ! LoadCommand.GetLoadPercent(Instant.now(), probe.ref)
      probe.expectMessage(100) // and stay 100 percent loaded

      loadActor ! LoadCommand.Unload(Instant.now())
      waitForClose(0)
      loadActor ! LoadCommand.GetLoadPercent(Instant.now(), probe.ref)
      probe.expectNoMessage() // actor should stop

      @tailrec
      def waitForOpen(targetPercentage: Double): Unit = {
        val now = Instant.now()
        loadActor ! LoadCommand.GetLoadPercent(now, probe.ref)
        val percentage: Double = probe.receiveMessage(50.millis)
        assert(percentage >= openRange._1)
        assert(percentage <= openRange._2)
        if (percentage < targetPercentage) {
          // println(s"opening: $percentage -> $targetPercentage")
          waitForOpen(targetPercentage)
        } else {
          println(
            s"target reached: $percentage -> $targetPercentage"
          )
        }
      }

      @tailrec
      def waitForClose(targetPercentage: Double): Unit = {
        val now = Instant.now()
        loadActor ! LoadCommand.GetLoadPercent(now, probe.ref)
        val percentage: Double = probe.receiveMessage(50.millis)
        assert(percentage >= openRange._1)
        assert(percentage <= openRange._2)
        if (percentage > targetPercentage) {
          // println(s"closing: $percentage -> $targetPercentage")
          waitForClose(targetPercentage)
        } else {
          println(
            s"target reached: $percentage -> $targetPercentage"
          )
        }
      }
    }
  }
}
