package fr.ribierjoakim.followuptrainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.{StdInReadLineHelpers, WriteJsonHelpers}

abstract class MainOption(config: Config) extends StdInReadLineHelpers with WriteJsonHelpers {

  def start
  def titleMenuKey : String

  def displayAndReadDistanceFromInputLine(config: Config): Double = {
    val distance = readString(config, "message.compute-run-times.distance.label")
    return getDistanceFromValue(distance)
  }

  private def getDistanceFromValue(value: String): Double = {
    try {
      value.toDouble
    } catch {
      case x: NumberFormatException => {
        value match {
          case "semi" => 21.097
          case "marathon" => 42.195
          case _ => throw x
        }
      }
      case x: Throwable => throw x
    }
  }
}
