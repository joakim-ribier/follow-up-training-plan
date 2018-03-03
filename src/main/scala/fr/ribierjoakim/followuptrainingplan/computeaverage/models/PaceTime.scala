package fr.ribierjoakim.followuptrainingplan.computeaverage.models

import fr.ribierjoakim.followuptrainingplan.common.utils.NumberFormatUtils._

object PaceTime {

  /**
    *
    * @param value
    * @return
    */
  def to(value: String): Option[PaceTime] = {
    value.split(":") match {
      case Array(h, m, s) => Some(new PaceTime(h.toInt, m.toInt, s.toInt))
      case Array(m, s) => Some(new PaceTime(0, m.toInt, s.toInt))
      case Array(s) => Some(new PaceTime(0, 0, s.toInt))
      case _ => None
    }
  }
}
case class PaceTime(val h: Int, val m: Int, val s: Int) {

  override def toString(): String = {
    (h, m, s) match {
      case (h, m, s) if h > 0 => s"${h}h ${format(m)}'${format(s)}"
      case (_, m, s) => s"${format(m)}'${format(s)}"
      case _ => ""
    }
  }
}
