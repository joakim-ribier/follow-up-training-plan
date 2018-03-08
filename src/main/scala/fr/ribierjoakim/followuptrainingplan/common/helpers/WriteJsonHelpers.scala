package fr.ribierjoakim.followuptrainingplan.common.helpers

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingPlan
import io.circe.syntax._

trait WriteJsonHelpers {

  def writeCurrentData(config: Config, trainingPlan: TrainingPlan): Boolean = {
    FileUtils.writeJson(config.getCurrentFilePath, trainingPlan.asJson)
  }
}
