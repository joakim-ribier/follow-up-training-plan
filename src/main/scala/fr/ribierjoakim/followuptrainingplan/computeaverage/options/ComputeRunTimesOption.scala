package fr.ribierjoakim.followuptrainingplan.computeaverage.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.utils.{PaceComputeUtils, TimeStringConverterUtils}
import fr.ribierjoakim.followuptrainingplan.computeaverage.models.PaceTime
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.screendrawing.{DrawerTable, TableRow}

class ComputeRunTimesOption(config: Config) extends MainOption(config) {

  override def start = {
    printTitle(config.getString("message.compute-run-times.title"))
    printLineBreak
    println(config.getString("message.compute-run-times.info-1"))
    println(config.getString("message.compute-run-times.info-2"))

    printLineBreak

    val distance: Double = displayAndReadDistanceFromInputLine(config)
    val time: Option[String] = readStringOpt(config, "message.compute-run-times.time.label")
    val pace: Option[String] = readStringOpt(config, "message.compute-run-times.pace.label")
    printLineBreak

    val (tablesNumberPerLine, rowsNumberPerTable) = displayAndReadTableConfigurationInputLine()

    val maybeSeconds: Option[BigDecimal] = (time, pace) match {
      case (Some(x), _) => TimeStringConverterUtils.convertToSeconds(x)
      case (_, Some(x)) => TimeStringConverterUtils.minPerKmToSeconds(x).map(_ * distance)
      case _ => None
    }

    maybeSeconds.map { seconds =>
      val paceTime: PaceTime = PaceComputeUtils.pacePerMinutes(distance, seconds)
      val hoursPerKm = PaceComputeUtils.pacePerHours(distance, seconds)

      val runTimes: Map[BigDecimal, PaceTime] = PaceComputeUtils.computeRunTimes(distance, seconds)

      val column1Values: Seq[TableRow] = runTimes.map(f => new TableRow(f._1.toString(), None)).toSeq
      val column2Values: Seq[TableRow] = runTimes.map(f => new TableRow(f._2.toString(), Some(Int))).toSeq

      val column1 = config.getString("message.km.label")
      val column2 = config.getString("message.run-times.label")
      val columnsHeader: Seq[String] = Seq(column1, column2)

      printInfo(config.getStringWithArgs("message.compute-average-pace.pace.result", paceTime.toString(), hoursPerKm))
      printLineBreak

      new DrawerTable(config)
        .withTablesNumberPerLine(tablesNumberPerLine)
        .withRowsNumberPerTable(rowsNumberPerTable)
        .drawTable(columnsHeader, column1Values, column2Values)
    }
  }

  private def displayAndReadTableConfigurationInputLine(): (Int, Int) = {
    val tablesNumberPerLine = config.getInt("configuration.screen-drawing.table.tables-number-per-line")
    val rowsNumberPerTable = config.getInt("configuration.screen-drawing.table.rows-number-per-table")

    print(config.getStringWithIntArgs("message.screen-drawing.choose-display-table-conf", tablesNumberPerLine, rowsNumberPerTable))

    val screenDrawingTableConf = scala.io.StdIn.readLine()
    val tab = screenDrawingTableConf.split(",")
    if (tab.length == 2) {
      (tab(0).toInt, tab(1).toInt)
    } else {
      (tablesNumberPerLine, rowsNumberPerTable)
    }
  }

  override def titleMenuKey : String = "message.option.compute-run-times.title"
}
