package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import java.nio.file.Path

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils._
import fr.ribierjoakim.followuptrainingplan.common.utils.{DateUtils, FileUtils}
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService

class UpdateHeaderTrainingPlanOption(config: Config) extends MainOption(config) {

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(trainingPlan) => {

        printValue("message.training-plan.plan-name.label", Some(trainingPlan.name))
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readString(config, "message.training-plan.set-plan-name.label")
          writeCurrentData(config, trainingPlan.copy(name = value))
        }

        printValue("message.training-plan.start-date.label", Some(DateUtils.print(trainingPlan.startDate, DateUtils.UI_DATE_PATTERN)))
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readDate(config, "message.training-plan.set-start-date.label")
          writeCurrentData(config, trainingPlan.copy(startDate = value))
        }

        printValue("message.training-plan.expected-time.label", Some(trainingPlan.expectedTime))
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readString(config, "message.training-plan.set-expected-time.label")
          writeCurrentData(config, trainingPlan.copy(expectedTime = value))
        }

        printValue("message.training-plan.hr-max.label", trainingPlan.hrMax)
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readIntOpt(config, "message.training-plan.set-hr-max.label")
          writeCurrentData(config, trainingPlan.copy(hrMax = value))
        }

        printValue("message.training-plan.hr-rest.label", trainingPlan.hrRest)
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readIntOpt(config, "message.training-plan.set-hr-rest.label")
          writeCurrentData(config, trainingPlan.copy(hrRest = value))
        }

        printValue("message.training-plan.comment.label", trainingPlan.comment)
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readStringOpt(config, "message.training-plan.set-comment.label")
          writeCurrentData(config, trainingPlan.copy(comment = value))
        }

        printValue("message.training-plan.plan-file.label", trainingPlan.plan)
        if (readBoolean(config, "message.app.update-by-continue")) {
          val value = readPathOpt(config, "message.training-plan.set-plan-file.label")
          trainingPlan.plan.map(x => FileUtils.delete(config.getFilePathFromCurrentDir(x)))
          val plan: Option[String] = value.flatMap { planPath =>
            val planPathFileName = formatFileName(planPath)
            val copyPlanPathToCurrentDir: Path = config.getFilePathFromCurrentDir(planPathFileName)
            copyFile(planPath, copyPlanPathToCurrentDir).map(_ => planPathFileName)
          }
          writeCurrentData(config, trainingPlan.copy(plan = plan))
        }
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  private def printValue(configKey: String, value: Option[Any]) = {
    printLineBreak
    print(config.getStringWithArgs(configKey, "**" + value.getOrElse("") + "**"))
    printLineBreak
  }

  override def titleMenuKey = "message.training-plan.option.update-header.title"
}
