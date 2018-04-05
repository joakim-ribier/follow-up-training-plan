package fr.ribierjoakim.followuptrainingplan.common.googledrive

import java.util.Collections

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class GoogleDriveApiService(val driveService: Drive) {

  private lazy val extensionToMimeType = Map(
    "json" -> "application/json",
    "pdf" -> "application/pdf",
    "default" -> "application/octet-stream")

  def createFolder(name: String, parentFolderId: String): Option[String] = {
    val file = new File();
    file.setName(name);
    file.setMimeType("application/vnd.google-apps.folder");
    file.setParents(Collections.singletonList(parentFolderId))

    val execute = driveService.files().create(file).setFields("id").execute()
    Option(execute.getId())
  }

  def updateFile(data: java.io.File, fileId: String): String = {
    val file = new File();
    file.setName(data.getName);
    file.setDescription("**File automatically uploaded from the application by the Google Drive API**")
    file.setMimeType(findMimeType(data))

    val fileContent = new FileContent(file.getMimeType, data)

    driveService.files().update(fileId, file, fileContent).execute().getId()
  }

  def createFile(data: java.io.File, parentFolderId: String): Option[String] = {
    val file = new File();
    file.setName(data.getName);
    file.setDescription("**File automatically uploaded from the application by the Google Drive API**")
    file.setMimeType(findMimeType(data))
    file.setParents(Collections.singletonList(parentFolderId))

    val fileContent = new FileContent(file.getMimeType, data)

    val execute = driveService.files().create(file, fileContent).setFields("id").execute()
    Option(execute.getId())
  }

  def removeFile(fileId: String) = {
    driveService.files.delete(fileId).execute()
  }

  private def findMimeType(file: java.io.File): String = {
    val defaultMimeType = extensionToMimeType.get("default").get
    lazy val ExtensionRegex = """^.*\.(.*)$""".r
    file.getName() match {
      case ExtensionRegex(extension) => extensionToMimeType.getOrElse(extension, defaultMimeType)
      case _ => defaultMimeType
    }
  }
}
