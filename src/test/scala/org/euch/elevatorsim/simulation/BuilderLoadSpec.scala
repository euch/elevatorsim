package org.euch.elevatorsim.simulation

import akka.actor.testkit.typed.scaladsl.{LogCapturing, ScalaTestWithActorTestKit, TestProbe}
import org.euch.elevatorsim.domain.model.Builder
import org.euch.elevatorsim.domain.model.dimensions.{DimensionsBox, DimensionsRectangle}
import org.euch.elevatorsim.domain.model.loads.{LoadGroup, Passenger}
import org.euch.elevatorsim.domain.model.winch.*
import org.euch.elevatorsim.simulation.order.keeper.BaseOrderKeeper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import java.time.Instant
import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.math.abs

class BuilderLoadSpec extends AnyFlatSpec with Matchers {

  private val passengerPeter = Passenger(
    name = "Peter",
    dimensions = DimensionsBox(30, 30, 180),
    weight = 100
  )
  private val passengerJohn = Passenger(
    name = "John",
    dimensions = DimensionsBox(40, 60, 180),
    weight = 100
  )
  private val passengerJames = Passenger(
    name = "James",
    dimensions = DimensionsBox(40, 60, 180),
    weight = 100
  )

  private val result = Builder.buildLoadGroup(
    List(passengerPeter, passengerJohn, passengerJames) zip List(1, 2, 1)
  )

  result.size shouldBe 2

  private val resGroup0 = result.head
  resGroup0.name.startsWith("lg_1_") shouldBe true
  resGroup0.elements shouldBe List(passengerPeter, passengerJames)

  private val resGroup1 = result(1)
  resGroup1.name.startsWith("lg_2_") shouldBe true
  resGroup1.elements shouldBe List(passengerJohn)
}
