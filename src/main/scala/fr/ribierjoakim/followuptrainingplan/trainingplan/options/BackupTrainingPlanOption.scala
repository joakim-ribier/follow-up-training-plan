package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import java.util.concurrent.Executors

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.components.LoadingScreenComponent
import fr.ribierjoakim.followuptrainingplan.common.googledrive.{GoogleDriveApiService, OAuth2GoogleDrive}
import fr.ribierjoakim.followuptrainingplan.common.utils.FileUtils
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService

import scala.concurrent.ExecutionContext

class BackupTrainingPlanOption(config: Config) extends MainOption(config) {

  implicit val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(trainingPlan) => {
        printInfo(config.getString("message.training-plan.drive.backup-start"))
        printLineBreak

        val service: GoogleDriveApiService = OAuth2GoogleDrive.service
        var mapFileNameToDriveId = trainingPlan.drive.getOrElse(Map())

        println(config.getStringWithArgs("message.training-plan.drive.backup-of", config.getCurrentDirPath))
        printLineBreak

        if (!mapFileNameToDriveId.contains(config.currentDirName)) {
          LoadingScreenComponent.start
          val driveFileId = service.createFolder(config.currentDirName, config.getSettingValue("drive.folder-id"))
          LoadingScreenComponent.stop
          mapFileNameToDriveId += (config.currentDirName -> driveFileId)
          println(config.getStringWithArgs("message.training-plan.drive.new-folder.label", driveFileId, config.currentDirName))
        }

        FileUtils.getFileList(config.getCurrentDirPath).foreach { file =>
          LoadingScreenComponent.start
          mapFileNameToDriveId.get(file.getName) match {
            case Some(driveId) => {
              val driveFileId = service.updateFile(file, driveId)
              LoadingScreenComponent.stop
              println(config.getStringWithArgs("message.training-plan.drive.update-file.label", driveFileId, file.getName))
            }
            case None => {
              val driveFileId = service.createFile(file, mapFileNameToDriveId.get(config.currentDirName).get)
              LoadingScreenComponent.stop
              mapFileNameToDriveId += (file.getName -> driveFileId)
              println(config.getStringWithArgs("message.training-plan.drive.new-file.label", driveFileId, file.getName))
            }
          }
        }

        printInfo(config.getString("message.training-plan.drive.backup-successful"))
        writeCurrentData(config, trainingPlan.copy(drive = Some(mapFileNameToDriveId)))
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  override def titleMenuKey = "message.training-plan.option.backup.title"
}
