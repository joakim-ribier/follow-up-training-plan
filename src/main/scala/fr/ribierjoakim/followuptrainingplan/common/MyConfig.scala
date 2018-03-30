package fr.ribierjoakim.followuptrainingplan.common

import java.nio.file.Paths

import com.typesafe.config.{Config, ConfigException}
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingDayType.TrainingDayType

object MyConfig {

  implicit def configToMyConfig(config: Config) = MyConfig(config)
}

case class MyConfig(config: Config) {

  val currentDirName = "current"

  def getStringOpt(path: String): Option[String] = {
    try {
      Some(config.getString(path))
    } catch {
      case _: ConfigException => None
    }
  }

  def getStringWithArgs(path: String, objects: Object*): String = {
    val value = config.getString(path)
    objects.foldLeft((0, value)) { case ((index, value), arg) =>
      val newValue = value.replace(s"{$index}", arg.toString)
      val newIndex = index + 1
      (newIndex, newValue)
    }._2
  }

  def getStringWithIntArgs(path: String, values: Int*): String = {
    val toObjects: Seq[BigDecimal] = values.map(x => BigDecimal(x))
    getStringWithArgs(path, toObjects:_*)
  }

  def getSettingValue(path: String): String = {
    config.getString(s"configuration.$path")
  }

  def getSettingValueWithArgs(path: String, objects: Object*): String = {
    getStringWithArgs(path, objects:_*)
  }

  def getFilePathFromCurrentDir(filename: String) = Paths.get(getCurrentDirPath.toFile.getAbsolutePath, filename)

  def getRootDataDirPath = Paths.get(getSettingValue("root-data-directory"))
  def getCurrentDirPath = Paths.get(getSettingValue("training-plan.current-directory"))
  def getCurrentFilePath = Paths.get(getSettingValue("training-plan.current-data-file"))

  object TrainingPlan {
    private val prefix = "message.training-plan"

    def getTrainingDayKeyByShortcut(shortcut: String): Option[String] = {
      val map = config.getObject(s"$prefix.training-day-type.shortcut.map")
      map.get(shortcut) match {
        case null => None
        case x => Some(x.unwrapped().toString)
      }
    }

    def getDisplayTrainingTypeValue(trainingDayType: TrainingDayType): String = {
      config.getString(s"$prefix.training-day.type.${trainingDayType.toString.toLowerCase}.label")
    }
  }
}