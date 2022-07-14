package org.euch.elevatorsim

import java.time.Instant

object InstantUtils {
  def diffSeconds(later: Instant, earlier: Instant): Double =
    (later.toEpochMilli - earlier.toEpochMilli) / 1000.0
}
