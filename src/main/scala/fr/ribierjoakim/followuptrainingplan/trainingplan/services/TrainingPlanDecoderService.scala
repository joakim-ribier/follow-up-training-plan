package fr.ribierjoakim.followuptrainingplan.trainingplan.services

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.exceptions.MyThrowable._
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingPlan
import io.circe.parser.decode

class TrainingPlanDecoderService(config: Config) {

  def getCurrentTrainingPlan: Option[TrainingPlan] = {
    val data: String = FileUtils.read(config.getCurrentFilePath)
    decode[TrainingPlan](data) match {
      case Left(error) => error.console; None
      case Right(trainingPlan) => Some(trainingPlan)
    }
  }
}
