package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import java.nio.file.Path

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.components.LoadingScreenComponent
import fr.ribierjoakim.followuptrainingplan.common.exceptions.ExitMenuException
import fr.ribierjoakim.followuptrainingplan.common.exceptions.MyThrowable._
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils.{createDirectory, _}
import fr.ribierjoakim.followuptrainingplan.common.utils.{DateUtils, HRComputeUtils}
import fr.ribierjoakim.followuptrainingplan.options.{ExitMenuOption, MainOption}
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingPlan

import scala.collection.immutable.ListMap

class MainTrainingPlanOption(config: Config) extends MainOption(config) {

  override def start = {
    if (isNewTrainingPlan) {

      printLineBreak
      val name = readString(config, "message.training-plan.set-plan-name.label")
      val startDate = readDate(config, "message.training-plan.set-start-date.label")

      printLineBreak
      val expectedTime = readString(config, "message.training-plan.set-expected-time.label")
      val hrMax = readIntOpt(config, "message.training-plan.set-hr-max.label")
      val hrRest = readIntOpt(config, "message.training-plan.set-hr-rest.label")

      printLineBreak
      val comment = readStringOpt(config, "message.training-plan.set-comment.label")
      val planPathToFollow = readPathOpt(config, "message.training-plan.set-plan-file.label")

      val trainingPlan = TrainingPlan(
        name = name,
        startDate = startDate,
        expectedTime = expectedTime,
        comment = comment,
        hrMax = hrMax,
        hrRest = hrRest)

      val success = initializeNewTrainingPlan(trainingPlan, planPathToFollow)
      if (success) {
        printInfo(name + " - " + DateUtils.print(startDate, DateUtils.UI_DATE_PATTERN))
        printLineBreak
        println(config.getString("message.training-plan.create-successful-info"))
        printLineBreak
        (hrMax, hrRest) match {
          case (Some(max), Some(rest)) => {
            val hrReserve = HRComputeUtils.hrReserve(max, rest)
            val footingPace = HRComputeUtils.hrFooting(hrReserve, rest)
            val marathonPace = HRComputeUtils.hrMarathon(hrReserve, rest)
            println(config.getStringWithArgs("message.training-plan.hr-reserve.label", BigDecimal(hrReserve)))
            println(config.getStringWithArgs("message.training-plan.footing-pace.label", BigDecimal(footingPace)))
            println(config.getStringWithArgs("message.training-plan.marathon-pace.label", BigDecimal(marathonPace._1), BigDecimal(marathonPace._2)))
          }
          case _ => println(config.getString("message.training-plan.hr-compute-no-data"))
        }
        printLineBreak
        readStringOpt(config, "message.app.press-key-to-continue")
      }
    }

    val menuOptions: ListMap[String, MainOption] = ListMap(
      "1" -> new DisplayTrainingPlanOption(config),
      "2" -> new UpdateHeaderTrainingPlanOption(config),
      "3" -> new ResumeTrainingPlanDayOption(config),
      "4" -> new BackupTrainingPlanOption(config),
      "5" -> new GenerateTrainingPlanPDFOption(config),
      "x" -> new ExitMenuOption(config))

    menu(config, menuOptions)
  }

  private def menu(config: Config, options: Map[String, MainOption]): Unit = {
    printLineBreak
    drawCharacters(config.getString("message.training-plan.title"))
    printLineBreak

    options.map { x  =>
      println(s"${x._1}. ${config.getString(x._2.titleMenuKey)}")
    }
    printLineBreak
    print(config.getString("message.menu.choose-an-option"))

    try {
      val value = scala.io.StdIn.readLine().toString
      options.getOrElse(value, options.get("x").get).start

      menu(config, options)
    } catch {
      case _: ExitMenuException => // Go back to the main menu
      case x: Throwable => {
        LoadingScreenComponent.stop
        x.console
        menu(config, options)
      }
    }
  }

  override def titleMenuKey : String = {
    if (isNewTrainingPlan) {
      "message.option.new-training-plan.title"
    } else {
      "message.option.resume-training-plan.title"
    }
  }

  private def initializeNewTrainingPlan(trainingPlan: TrainingPlan, maybePlanPath: Option[Path]): Boolean = {
    (for {
      _ <- createDirectory(config.getCurrentDirPath)
      maybeDataFile = createFile(config.getCurrentFilePath)
      if maybeDataFile.isDefined
    } yield {
      val maybeTrainingPlanUpdated = maybePlanPath.flatMap { planPath =>
        val planPathFileName = formatFileName(planPath)
        val copyPlanPathToCurrentDir: Path = config.getFilePathFromCurrentDir(planPathFileName)
        copyFile(planPath, copyPlanPathToCurrentDir).map(_ => trainingPlan.copy(plan = Some(planPathFileName)))
      }
      writeCurrentData(config, maybeTrainingPlanUpdated.getOrElse(trainingPlan))
    }).getOrElse(false)
  }

  private def isNewTrainingPlan: Boolean = !exists(config.getCurrentDirPath)
}
