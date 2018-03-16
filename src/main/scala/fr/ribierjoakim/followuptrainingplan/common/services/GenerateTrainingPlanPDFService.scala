package fr.ribierjoakim.followuptrainingplan.common.services

import java.io.FileOutputStream

import com.itextpdf.text._
import com.itextpdf.text.pdf._
import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig._
import fr.ribierjoakim.followuptrainingplan.common.helpers.ITextHelpers
import fr.ribierjoakim.followuptrainingplan.common.utils.{DateUtils, HRComputeUtils, NumberFormatUtils}
import fr.ribierjoakim.followuptrainingplan.computeaverage.models.PaceTime
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.{TrainingDay, TrainingDayType, TrainingPlan}

import scala.collection.immutable.ListMap

class GenerateTrainingPlanPDFService(config: Config) extends ITextHelpers {

  def process(trainingPlan: TrainingPlan): String = {
      val document: Document = new Document()

      val fileName = s"${config.getCurrentDirPath}/${trainingPlan.formatNameToFileName}-gen.pdf"
      val writer: PdfWriter = PdfWriter.getInstance(document, new FileOutputStream(fileName))

      document.open()

      writer.setPageEvent(new OnEndPagePdfPageEvent(trainingPlan, config))

      addFirstPage(document, trainingPlan, config)
      document.newPage()

      val anchorMapPage: Map[String, String] = addMenuPage(document, config)
      document.newPage()

      addFollowUpPage(document, trainingPlan, anchorMapPage, config)

      document.close()
      fileName
  }

  private def addFirstPage(document: Document, trainingPlan: TrainingPlan, config: Config) = {
    val dataTable = new PdfPTable(1)
    dataTable.addCell(getCell(trainingPlan.name.toUpperCase, padding = 15.0f, hzAlign = Element.ALIGN_CENTER, size = 22, color = blue))
    dataTable.addCell(getCell("-", padding = 15.0f, hzAlign = Element.ALIGN_CENTER, size = 22))
    dataTable.addCell(getCell(config.getString("message.training-plan.pdf.page.training").toUpperCase, padding = 15.0f, hzAlign = Element.ALIGN_CENTER, size = 18))
    dataTable.addCell(getCell("Y(^_^)Y", padding = 15.0f, hzAlign = Element.ALIGN_CENTER, size = 18))

    val expectedTimeFormat = PaceTime.to(trainingPlan.expectedTime).map(_.toString).getOrElse("")
    dataTable.addCell(getCell(expectedTimeFormat.toUpperCase, padding = 15.0f, hzAlign = Element.ALIGN_CENTER, bold = true, size = 42, color = blue))

    val cell = new PdfPCell(dataTable)
    cell.setBorder(Rectangle.NO_BORDER)
    cell.setFixedHeight(document.top() - document.topMargin())
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE)

    val rootTable = new PdfPTable(1)
    rootTable.addCell(cell)

    document.add(rootTable)
  }

  private def addMenuPage(document: Document, config: Config): Map[String, String] = {
    val titleTable = new PdfPTable(1)
    titleTable.addCell(getTitlePageCell(config.getString("message.training-plan.pdf.page.menu")))
    document.add(titleTable)

    val width = document.right() - document.left()
    val columnWidth: Array[Float] = Array(
      (width * 8) / 100,
      (width * 92) / 100)

    val menuTable = new PdfPTable(2)
    menuTable.setWidths(columnWidth)
    menuTable.setSpacingBefore(30.0f)
    menuTable.setSpacingAfter(30.0f)

    menuTable.addCell(getCell("1."))
    menuTable.addCell(getCell(config.getString("message.training-plan.pdf.page.training"), anchorRef = Some("#trainingPageTarget")))

    document.add(menuTable)

    Map("trainingPage" -> "trainingPageTarget")
  }

  private def addFollowUpPage(document: Document, trainingPlan: TrainingPlan, anchorMapPage: Map[String, String], config: Config) = {
    val titleTable = new PdfPTable(1)
    titleTable.addCell(getTitlePageCell(config.getString("message.training-plan.pdf.page.training"), anchorMapPage.get("trainingPage")))
    document.add(titleTable)

    val infoTable = new PdfPTable(1)
    infoTable.setSpacingBefore(30.0f)
    infoTable.setSpacingAfter(30.0f)

    trainingPlan.comment.map { value =>
      infoTable.addCell(getCell(value))
      infoTable.addCell(getCell(" "))
    }

    val expectedTimeFormat = PaceTime.to(trainingPlan.expectedTime).map(_.toString).getOrElse("")
    infoTable.addCell(getCell(config.getStringWithArgs("message.training-plan.view.expectedTime", expectedTimeFormat)))

    (trainingPlan.hrMax, trainingPlan.hrRest) match {
      case (Some(max), Some(rest)) => {
        val hrReserve = HRComputeUtils.hrReserve(max, rest)
        val footingPace = HRComputeUtils.hrFooting(hrReserve, rest)
        val marathonPace = HRComputeUtils.hrMarathon(hrReserve, rest)

        infoTable.addCell(getCell(" "))
        infoTable.addCell(getCell(config.getStringWithArgs("message.training-plan.hr-reserve.label", hrReserve.toString)))
        infoTable.addCell(getCell(config.getStringWithArgs("message.training-plan.footing-pace.label", footingPace.toString)))
        infoTable.addCell(getCell(config.getStringWithArgs("message.training-plan.marathon-pace.label", marathonPace._1.toString, marathonPace._2.toString)))
      }
      case _ => infoTable.addCell(getCell(config.getString("message.training-plan.hr-compute-no-data")))
    }
    document.add(infoTable)

    val width = document.right() - document.left()
    val columnWidth: Array[Float] = Array(
      (width * 15) / 100,
      (width * 10) / 100,
      (width * 25) / 100,
      (width * 10) / 100,
      (width * 20) / 100,
      (width * 20) / 100)

    val sortedWeeks: Map[String, Seq[TrainingDay]] = ListMap(trainingPlan.weeks.toSeq.sortBy(_._1): _*)
    sortedWeeks.foreach {
      case (nbWeek, days) => {
        val weekTable = new PdfPTable(6)
        weekTable.setWidthPercentage(100)
        weekTable.setSpacingBefore(10.0f)
        weekTable.setSpacingAfter(5.0f)
        weekTable.setTotalWidth(columnWidth)

        // header title
        val runTotalActivities = days.filter(x => x.`type`.toString ==  TrainingDayType.RUNNING.toString)
        val runTotalKms = NumberFormatUtils.round(runTotalActivities.map(_.km.getOrElse(0.0)).sum)
        val headerTableValue = config.getStringWithArgs("message.training-plan.view.table-title", nbWeek, runTotalActivities.size.toString, runTotalKms.toString)

        val headerCell = getCell(headerTableValue, hzAlign = Element.ALIGN_RIGHT, color = blue, padding = 0.0f)
        headerCell.setColspan(6)
        weekTable.addCell(headerCell)

        // header columns
        weekTable.addCell(getCell(config.getString("message.date.label"), hzAlign = Element.ALIGN_CENTER))
        weekTable.addCell(getCell(config.getString("message.type.label"), hzAlign = Element.ALIGN_CENTER))
        weekTable.addCell(getCell(config.getString("message.label.label")))
        weekTable.addCell(getCell(config.getString("message.km.label"), hzAlign = Element.ALIGN_CENTER))
        weekTable.addCell(getCell(config.getString("message.comment.label")))
        weekTable.addCell(getCell(config.getString("message.site.label")))

        // table data
        days.foldLeft(0) { case (cpt, day) =>
          val backColor: Boolean = (cpt % 2) == 0
          weekTable.addCell(getCell(DateUtils.UI_DATE_TIME_FORMATTER.print(day.dateTime), bckColor = backColor, hzAlign = Element.ALIGN_CENTER))
          weekTable.addCell(getCell(config.getString(s"message.training-day.type.${day.`type`.toString.toLowerCase}.label"), bckColor = backColor, hzAlign = Element.ALIGN_CENTER))
          weekTable.addCell(getCell(day.label, bckColor = backColor))
          weekTable.addCell(getCell(day.km.map(_.toString).getOrElse(""), bckColor = backColor, hzAlign = Element.ALIGN_CENTER))
          weekTable.addCell(getCell(day.comment.getOrElse(""), bckColor = backColor))
          weekTable.addCell(getCell(day.site.getOrElse(""), bckColor = backColor))
          cpt + 1
        }
        document.add(weekTable)
      }
    }
  }

  private class OnEndPagePdfPageEvent(trainingPlan: TrainingPlan, config: Config) extends PdfPageEventHelper {

    private val font = FontFactory.getFont(defaultFont, 8, Font.ITALIC, BaseColor.BLACK)

    override def onEndPage(pdfWriter: PdfWriter, document: Document) {
      val pdfContentByte = pdfWriter.getDirectContent()

      val header = new Phrase(s"${trainingPlan.name} - ${config.getString("message.training-plan.pdf.preparation.title")}", font)
      val footer = new Phrase(config.getString("message.app.copyright"), font)

      ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER,
        header,
        (document.right() - document.left()) / 2 + document.leftMargin(),
        document.top() + 10, 0)

      ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_CENTER,
        footer,
        (document.right() - document.left()) / 2 + document.leftMargin(),
        document.bottom() - 10, 0)
    }
  }
}