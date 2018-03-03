package fr.ribierjoakim.followuptrainingplan.common

import java.nio.file.Path

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.utils.{DateUtils, FileUtils}
import org.joda.time.DateTime

trait StdInReadLineHelpers {

  def readBoolean(config: Config, keyLabel: String): Boolean = {
    readStringOpt(config, keyLabel) match {
      case Some(x) if x == "1" => true
      case _ => false
    }
  }

  def readString(config: Config, keyLabel: String): String = {
    readStringOpt(config ,keyLabel).getOrElse(throw new IllegalArgumentException(s"value for '$keyLabel' is required"))
  }

  def readStringOpt(config: Config, keyLabel: String): Option[String] = {
    print(config.getString(keyLabel))
    scala.io.StdIn.readLine() match {
      case x if x.isEmpty => None
      case x => Some(x)
    }
  }

  def readDoubleOpt(config: Config, keyLabel: String): Option[Double] = {
    readStringOpt(config, keyLabel).map(_.toDouble)
  }

  def readIntOpt(config: Config, keyLabel: String): Option[Int] = {
    readStringOpt(config, keyLabel).map(_.toInt)
  }

  def readPathOpt(config: Config, keyLabel: String): Option[Path] = {
    readStringOpt(config, keyLabel).flatMap(FileUtils.get(_))
  }

  def readDateTime(config: Config, keyLabel: String): DateTime = {
    DateUtils.parse(readString(config, keyLabel), DateUtils.UI_DATE_TIME_FORMATTER)
  }

  def readDate(config: Config, keyLabel: String): DateTime = {
    DateUtils.parse(readString(config, keyLabel), DateUtils.UI_DATE_PATTERN)
  }
}
