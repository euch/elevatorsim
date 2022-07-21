package org.euch.elevatorsim

import java.time.Instant

object PercentUtils {

  val p0: Int = 0
  val p100: Int = 100

  def travelPercentageLTE100(
      durationSeconds: Double,
      percentPerSecond: Double
  ): Double = travelPercentageLTE(durationSeconds, percentPerSecond, p100)

  def travelPercentageGTE0(
      durationSeconds: Double,
      percentPerSecond: Double
  ): Double = travelPercentageGTE(durationSeconds, percentPerSecond, p0)

  def travelPercentageLTE(
      durationSeconds: Double,
      percentPerSecond: Double,
      lte: Double
  ): Double = math.min(travelPercentage(durationSeconds, percentPerSecond), lte)

  def travelPercentageGTE(
      durationSeconds: Double,
      percentPerSecond: Double,
      gte: Double
  ): Double = math.max(travelPercentage(durationSeconds, percentPerSecond), gte)

  def travelPercentage(
      durationSeconds: Double,
      percentPerSecond: Double
  ): Double = durationSeconds * percentPerSecond

}
