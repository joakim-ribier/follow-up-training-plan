package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.utils.{DateUtils, HRComputeUtils, NumberFormatUtils}
import fr.ribierjoakim.followuptrainingplan.computeaverage.models.PaceTime
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.screendrawing.{DrawerTable, TableRow}
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.{TrainingDay, TrainingDayType, TrainingPlan}
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService

import scala.collection.immutable.ListMap

class DisplayTrainingPlanOption(config: Config) extends MainOption(config) {

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(trainingPlan) => {

        displayHeaderInfo(trainingPlan)
        printLineBreak
        displayData(trainingPlan)

        printLineBreak
        readStringOpt(config, "message.app.press-key-to-continue")
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  private def displayData(trainingPlan: TrainingPlan) = {
    val drawerTable = new DrawerTable(config).withTablesNumberPerLine(1).withRowsNumberPerTable(10)

    val sortedWeeks: Map[String, Seq[TrainingDay]] = ListMap(trainingPlan.weeks.toSeq.sortBy(_._1): _*)

    val columnsHeader: Seq[String] = Seq(
      config.getString("message.date.label"),
      config.getString("message.type.label"),
      config.getString("message.label.label"),
      config.getString("message.km.label"),
      config.getString("message.comment.label"),
      config.getString("message.site.label"))

    sortedWeeks.foreach { case (nbWeek, days) => {

        def buildTableRow(trainingDay: TrainingDay, value: String): TableRow = {
          if (trainingDay.`type` == TrainingDayType.REST) {
            new TableRow("", None, TableRow.fillWithDotChar)
          } else {
            new TableRow(value, None, TableRow.fillWithEmptyChar)
          }
        }
        val allDaysOfWeek: Seq[TrainingDay] = fillRestDay(trainingPlan, days)

        val runTotalActivities = allDaysOfWeek.filter(x => x.`type`.toString ==  TrainingDayType.RUNNING.toString)
        val runTotalKms = NumberFormatUtils.round(runTotalActivities.map(_.km.getOrElse(0.0)).sum)

        val labelValues = allDaysOfWeek.map(day => buildTableRow(day, day.label))
        val dateValues = allDaysOfWeek.map(day => buildTableRow(day, DateUtils.UI_DATE_TIME_FORMATTER.print(day.dateTime)))
        val commentValues = allDaysOfWeek.map(day => buildTableRow(day, day.comment.getOrElse("")))
        val siteValues = allDaysOfWeek.map(day => buildTableRow(day, day.site.getOrElse("")))

        val typeValues = allDaysOfWeek.map { x =>
          val value = config.getString(s"message.training-day.type.${x.`type`.toString.toLowerCase}.label")
          new TableRow(value, None, TableRow.fillWithEmptyChar)
        }

        val kmValues = allDaysOfWeek.map { day =>
          if (day.`type` == TrainingDayType.REST) {
            new TableRow("", None, TableRow.fillWithDotChar)
          } else {
            day.km match {
              case Some(x) => new TableRow(NumberFormatUtils.round(x).toString, None, TableRow.fillWithEmptyChar)
              case None => new TableRow("", None, TableRow.fillWithEmptyChar)
            }
          }
        }

        val headerName = config.getStringWithArgs("message.training-plan.view.table-title", nbWeek, runTotalActivities.size.toString, runTotalKms.toString)
        drawerTable
          .withHeaderName(Some(headerName))
          .drawTable(columnsHeader, dateValues, typeValues, labelValues, kmValues, commentValues, siteValues)
      }
    }
  }

  private def fillRestDay(trainingPlan: TrainingPlan, days: Seq[TrainingDay]): Seq[TrainingDay] = {
    val firstDayOfWeek = trainingPlan.startDate.getDayOfWeek
    val endDayOfWeek = firstDayOfWeek + 6

    var fillMapWithEmptyDays: Map[Int, TrainingDay] = Map()
    for (i <- firstDayOfWeek to endDayOfWeek) {
      fillMapWithEmptyDays += (i -> TrainingDay("", DateUtils.epoch, `type` = TrainingDayType.REST))
    }

    // replace empty day by the right day if it exists
    days.foreach { day =>
      fillMapWithEmptyDays += (day.dateTime.getDayOfWeek -> day)
    }

    // finally sort the values by the dayOfWeek (1, 2, 3...)
    ListMap(fillMapWithEmptyDays.toSeq.sortBy(_._1):_*).values.toSeq
  }

  private def displayHeaderInfo(trainingPlan: TrainingPlan) = {
    printTitle(trainingPlan.name)

    trainingPlan.comment.map { comment =>
      printLineBreak
      println(comment)
    }

    PaceTime.to(trainingPlan.expectedTime).map { value =>
      printLineBreak
      println(config.getStringWithArgs("message.training-plan.view.expectedTime", value))
    }

    (trainingPlan.hrMax, trainingPlan.hrRest) match {
      case (Some(max), Some(rest)) => {
        val hrReserve = HRComputeUtils.hrReserve(max, rest)
        val footingPace = HRComputeUtils.hrFooting(hrReserve, rest)
        val marathonPace = HRComputeUtils.hrMarathon(hrReserve, rest)

        printLineBreak
        println(config.getStringWithArgs("message.training-plan.hr-reserve.label", hrReserve.toString))
        println(config.getStringWithArgs("message.training-plan.footing-pace.label", footingPace.toString))
        println(config.getStringWithArgs("message.training-plan.marathon-pace.label", marathonPace._1.toString, marathonPace._2.toString))
      }
      case _ => println(config.getString("message.training-plan.hr-compute-no-data"))
    }
  }

  override def titleMenuKey = "message.training-plan.option.display.title"
}
