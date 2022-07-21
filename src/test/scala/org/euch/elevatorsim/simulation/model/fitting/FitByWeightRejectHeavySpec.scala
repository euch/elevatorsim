package org.euch.elevatorsim.simulation.model.fitting

import org.euch.elevatorsim.domain.model.body.BoxBody
import org.euch.elevatorsim.domain.model.dimensions.DimensionsBox
import org.euch.elevatorsim.domain.model.loads.*
import org.euch.elevatorsim.domain.model.transport.CabinBox
import org.euch.elevatorsim.simulation.model.fitting.FitResult
import org.euch.elevatorsim.simulation.model.fitting.weight.FitByWeightRejectHeavy
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FitByWeightRejectHeavySpec extends AnyFlatSpec with Matchers {
  private val cabin = new CabinBox(
    "cabin",
    weight = 100,
    dimensions = new DimensionsBox(200, 200, 200),
    innerDimensions = new DimensionsBox(200, 200, 200),
    defaultFloor = Some("1")
  )
  private val groupA = LoadGroup(
    "GroupA",
    "1",
    List(
      Passenger(
        name = "Peter",
        dimensions = DimensionsBox(30, 30, 180),
        weight = 100
      )
    ),
    50,
    50
  )
  new FitByWeightRejectHeavy(200).fit(cabin, List(groupA)) shouldBe
    FitResult(fits = List(groupA), rejected = List.empty)

  private val groupB = LoadGroup(
    "GroupB",
    "1",
    List(
      Passenger(
        name = "John",
        dimensions = DimensionsBox(30, 30, 180),
        weight = 150
      )
    ),
    50,
    50
  )
  private val groupC = LoadGroup(
    "GroupC",
    "1",
    List(
      Passenger(
        name = "James",
        dimensions = DimensionsBox(30, 30, 180),
        weight = 100
      )
    ),
    50,
    50
  )
  new FitByWeightRejectHeavy(300)
    .fit(cabin, List(groupA, groupB, groupC)) shouldBe
    FitResult(fits = List(groupA, groupC), rejected = List(groupB))
}
