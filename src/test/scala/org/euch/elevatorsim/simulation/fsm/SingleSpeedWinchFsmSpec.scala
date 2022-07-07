package org.euch.elevatorsim.simulation.fsm

import org.euch.elevatorsim.simulation.model.Order
import org.euch.elevatorsim.domain.model.winch.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant

class SingleSpeedWinchFsmSpec extends AnyFlatSpec with Matchers {
  val testWinch: Winch =
    SingleSpeedWinch(nominalSpeedUp = 1d, nominalSpeedDown = -2d)
  val sut: WinchFSM = WinchFSM(testWinch)

  // idle
  sut.getSpeed(Instant.now) shouldBe 0d
  sut.stop(Instant.now)
  sut.getSpeed(Instant.now) shouldBe 0d
  // started (up)
  sut.start(Order.NormalUp, Instant.now)
  sut.getSpeed(Instant.now) shouldBe 1d
  // rejects commends (going up)
  sut.start(Order.NormalUp, Instant.now)
  sut.getSpeed(Instant.now) shouldBe 1d
  sut.start(Order.NormalDown, Instant.now)
  sut.getSpeed(Instant.now) shouldBe 1d
  // stopped
  sut.stop(Instant.now)
  sut.getSpeed(Instant.now) shouldBe 0d
  // started (down)
  sut.start(Order.NormalDown, Instant.now)
  sut.getSpeed(Instant.now) shouldBe -2d
  // rejects commands (going down)
  sut.start(Order.NormalUp, Instant.now)
  sut.getSpeed(Instant.now) shouldBe -2d
  sut.start(Order.NormalDown, Instant.now)
  sut.getSpeed(Instant.now) shouldBe -2d
  // stopped
  sut.stop(Instant.now)
  sut.getSpeed(Instant.now) shouldBe 0d
}
