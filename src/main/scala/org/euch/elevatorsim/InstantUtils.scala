package org.euch.elevatorsim

import java.time.Instant

object InstantUtils {
  def diffSeconds(i0: Instant, i1: Instant): Double = (i0.toEpochMilli - i1.toEpochMilli) / 1000.0
}
