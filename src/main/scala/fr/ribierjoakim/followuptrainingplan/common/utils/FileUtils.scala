package fr.ribierjoakim.followuptrainingplan.common.utils

import java.io.File
import java.nio.file._

import fr.ribierjoakim.followuptrainingplan.common.exceptions.MyThrowable._
import io.circe.{Json, Printer}

object FileUtils {

  private lazy val printer = Printer.noSpaces.copy(dropNullValues = true)

  def get(path: String): Option[Path] = {
    try {
      Some(Paths.get(path))
    } catch {
      case t : Throwable => t.console; None
    }
  }

  def formatFileName(path: Path): String = {
    path.getFileName().toString.replaceAll(" ", "-").toLowerCase
  }

  def createDirectory(path: Path): Option[Path] = {
    try {
      Some(Files.createDirectory(path))
    } catch {
      case t : Throwable => t.console; None
    }
  }

  def createFile(path: Path): Option[Path] = {
    try {
      Some(Files.createFile(path))
    } catch {
      case t : Throwable => t.console; None
    }
  }

  def copyFile(from: Path, to: Path): Option[Path] = {
    try {
      Some(Files.copy(from, to))
    } catch {
      case t : Throwable => t.console; None
    }
  }

  def delete(path: Path) = {
    try {
      Files.deleteIfExists(path)
    } catch {
      case t : Throwable => t.console
    }
  }

  def exists(path: Path): Boolean = Files.exists(path)

  def read(path: Path): String = new String(Files.readAllBytes(path))

  def writeJson(path: Path, json: Json): Boolean = write(path, printer.pretty(json))
  private def write(path: Path, data: String): Boolean = {
    try {
      Files.write(path, data.getBytes); true
    } catch {
      case t : Throwable => t.console; false
    }
  }

  def getFileList(path: Path): List[File] = {
    val file = path.toFile
    if (file.exists && file.isDirectory) {
      file.listFiles.filter(_.isFile).toList
    } else {
      List()
    }
  }
}
