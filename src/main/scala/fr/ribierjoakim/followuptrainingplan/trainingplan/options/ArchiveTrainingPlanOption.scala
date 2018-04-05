package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import java.io.FileNotFoundException
import java.nio.file.{Path, Paths}
import java.time.LocalDateTime
import java.util.concurrent.Executors

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.components.LoadingScreenComponent
import fr.ribierjoakim.followuptrainingplan.common.exceptions.ExitMenuException
import fr.ribierjoakim.followuptrainingplan.common.googledrive.{GoogleDriveApiService, OAuth2GoogleDrive}
import fr.ribierjoakim.followuptrainingplan.common.services.GenerateTrainingPlanPDFService
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingPlan
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService

import scala.concurrent.ExecutionContext

class ArchiveTrainingPlanOption(config: Config) extends MainOption(config) {

  implicit val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(x) => {

        printInfo(config.getString("message.training-plan.archive.start"))
        printLineBreak

        val goalAchieved = readBoolean(config, "message.training-plan.archive.goal-achieved.label")
        val resultTime = readString(config, "message.training-plan.archive.result-time.label")
        printLineBreak
        val resultComment = readString(config, "message.training-plan.archive.result-comment.label")
        printLineBreak

        // Step 1 - Update Training Plan
        val trainingPlan = updateTrainingPlan(x, resultTime, goalAchieved, resultComment)

        // Step 2 - Generate PDF
        generatePDF(trainingPlan)

        // Step 3 - Local backup
        LoadingScreenComponent.start
        val now = LocalDateTime.now()

        // Create year folder (~/2018/) if does not exist
        val yearPath = Paths.get(s"${config.getRootDataDirPath}/${now.getYear}")
        if (!FileUtils.exists(yearPath)) {
          if (FileUtils.createDirectory(yearPath).isEmpty) {
            throw new FileNotFoundException(s"${yearPath} not exists")
          }
        }

        // Create archive training plan folder (~/2018/UTMB/)
        val archivePath = Paths.get(s"$yearPath/${trainingPlan.formatNameToFileName}")
        FileUtils.move(config.getCurrentDirPath, archivePath) match {
          case Some(_) => {
            LoadingScreenComponent.stop
            println(config.getString("message.training-plan.archive.step-3"))

            // Step 4 - Cloud backup
            driveBackup(trainingPlan, now, archivePath)
            println(config.getString("message.training-plan.archive.step-4"))

            // Step 5 - Clean Google Drive 'current' folder
            cleanGoogleDriveCurrentFolder(trainingPlan)
            println(config.getString("message.training-plan.archive.step-5"))

            printInfo(config.getString("message.training-plan.archive.successful"))
            throw new ExitMenuException
          }
          case None => {
            LoadingScreenComponent.stop
            printInfo(config.getString("message.training-plan.archive.failed"))
          }
        }
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  private def updateTrainingPlan(trainingPlan: TrainingPlan, resultTime: String, goalAchieved: Boolean, resultComment: String): TrainingPlan = {
    LoadingScreenComponent.start

    val trainingPlanUpdated = trainingPlan.copy(
      resultTime = Some(resultTime),
      goalAchieved = goalAchieved,
      resultComment = Some(resultComment))

    writeCurrentData(config, trainingPlanUpdated)

    LoadingScreenComponent.stop
    println(config.getString("message.training-plan.archive.step-1"))

    trainingPlanUpdated
  }

  private def generatePDF(trainingPlan: TrainingPlan) = {
    LoadingScreenComponent.start
    new GenerateTrainingPlanPDFService(config).process(trainingPlan)
    LoadingScreenComponent.stop
    println(config.getString("message.training-plan.archive.step-2"))
  }

  private def driveBackup(trainingPlan: TrainingPlan, now: LocalDateTime, archivePath: Path) = {
    val service: GoogleDriveApiService = OAuth2GoogleDrive.service

    val yearsToDriveIdMap = config.getObject("configuration.drive.year-to-drive-id.map")
    val value = yearsToDriveIdMap.get(now.getYear.toString)
    if (value == null) {
      throw new IllegalArgumentException(s"drive id folder configuration for '${now.getYear}' does not exist")
    }

    println(config.getStringWithArgs("message.training-plan.archive.backup-of", archivePath))
    LoadingScreenComponent.start

    service.createFolder(trainingPlan.formatNameToFileName, value.unwrapped().toString) match {
      case Some(driveNewFolderId) => {
        LoadingScreenComponent.stop

        println(config.getStringWithArgs("message.training-plan.archive.new-folder.label", driveNewFolderId, trainingPlan.formatNameToFileName))
        FileUtils.getFileList(archivePath).foreach { file =>
          LoadingScreenComponent.start

          service.createFile(file, driveNewFolderId) match {
            case Some(driveFileId) => {
              LoadingScreenComponent.stop
              println(config.getStringWithArgs("message.training-plan.archive.new-file.label", driveFileId, file.getName))
            }
            case None => {
              LoadingScreenComponent.stop
              printError(s"error to upload '${file.getName()}' to '$driveNewFolderId'")
            }
          }
        }
      }
      case _ => {
        LoadingScreenComponent.stop
        printError(s"error to create '${trainingPlan.formatNameToFileName}' remote folder in '${value.unwrapped().toString}'")
      }
    }
  }

  private def cleanGoogleDriveCurrentFolder(trainingPlan: TrainingPlan) = {
    val service: GoogleDriveApiService = OAuth2GoogleDrive.service

    var mapFileNameToDriveId = trainingPlan.drive.getOrElse(Map())
    mapFileNameToDriveId.get(config.currentDirName) match {
      case Some(fileId) => {
        service.removeFile(fileId)
      }
      case _ => throw new IllegalArgumentException(s"operation not supported current folder not exists")
    }
  }

  override def titleMenuKey = "message.training-plan.option.archive.title"
}
