package fr.ribierjoakim.followuptrainingplan.computeaverage.models

import fr.ribierjoakim.followuptrainingplan.common.utils.NumberFormatUtils._

object PaceTime {

  def to(value: String): Option[PaceTime] = {
    value.split(":") match {
      case Array(h, m, s) => Some(new PaceTime(h.toInt, m.toInt, s.toInt))
      case Array(m, s) => Some(new PaceTime(0, m.toInt, s.toInt))
      case Array(s) => Some(new PaceTime(0, 0, s.toInt))
      case _ => throw new IllegalArgumentException("not supported by the application (just to manage default case)")
    }
  }
}
case class PaceTime(val h: Int, val m: Int, val s: Int) {

  override def toString(): String = {
    (h, m, s) match {
      case (h, m, s) if h > 0 => s"${h}h ${format(m)}'${format(s)}"
      case (_, m, s) if m > 0 => s"${format(m)}'${format(s)}"
      case (_, _, s) => s"${format(s)}"
      case _ => throw new IllegalArgumentException("not supported by the application (just to manage default case)")
    }
  }
}
