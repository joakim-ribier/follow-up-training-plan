package fr.ribierjoakim.followuptrainingplan.common.utils

object NumberFormatUtils {

  def format(value: Int): String = {
    value match {
      case x if x < 10 => s"0$x"
      case x => x.toString()
    }
  }

  def round(value: Double): Double = {
    BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
}
