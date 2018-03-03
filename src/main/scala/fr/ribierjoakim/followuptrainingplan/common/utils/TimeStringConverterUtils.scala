package fr.ribierjoakim.followuptrainingplan.common.utils

object TimeStringConverterUtils {

  /**
    * Convert min/km (3'30) to seconds
    * @param value min/km
    * @return seconds
    */
  def minPerKmToSeconds(value: String) : Option[BigDecimal] = {
    value.split("'") match {
      case Array(m, s) => convertToSeconds("0", m, s)
      case Array(m) => convertToSeconds("0", m, "0")
      case _ => None
    }
  }

  def convertToSeconds(h: String, m: String, s: String) : Option[BigDecimal] = {
    try {
      Some((h.toInt * 60 * 60) + m.toInt * 60 + s.toInt)
    } catch {
      case _: Throwable => None
    }
  }

  def convertToSeconds(value: String) : Option[BigDecimal] = {
    value.split(":") match {
      case Array(h, m, s) => convertToSeconds(h, m, s)
      case Array(m, s) => convertToSeconds("0", m, s)
      case Array(s) => convertToSeconds("0", "0", s)
      case _ => None
    }
  }
}
