package fr.ribierjoakim.followuptrainingplan.common.utils


import fr.ribierjoakim.followuptrainingplan.computeaverage.models.PaceTime

import scala.collection.immutable.ListMap

object PaceComputeUtils {

  def convertSecondsToPacePerMinute(seconds: BigDecimal) : PaceTime = {
    val h = seconds / 3600;
    var m = seconds / 3600 * 60;
    if (m > 59) {
      m = m % 60
    }
    val s = seconds % 60;
    PaceTime(h.toInt, m.toInt, s.toInt)
  }

  def pacePerMinutes(km: Double, seconds: BigDecimal) : PaceTime = {
    convertSecondsToPacePerMinute(seconds / km)
  }

  def pacePerHours(km: Double, seconds: BigDecimal) : BigDecimal = {
    val pace = km / seconds
    BigDecimal((pace * 3600).toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  }

  def computeRunTimes(km: Double, seconds: BigDecimal) : ListMap[BigDecimal, PaceTime] = {
    val pace = seconds / km
    if (km > 1) {
      var map: ListMap[BigDecimal, PaceTime] = ListMap()
      for (i <- 1 to km.toInt) {
        val time = i * pace
        val paceTime = convertSecondsToPacePerMinute(time)
        map += (BigDecimal(i) -> paceTime)
      }
      if (map.size < km) {
        val paceTime = convertSecondsToPacePerMinute(seconds)
        map += (BigDecimal(km) -> paceTime)
      }
      map
    } else {
      ListMap(BigDecimal(km) -> PaceTime(0, 0, seconds.toInt))
    }
  }
}