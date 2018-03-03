package fr.ribierjoakim.followuptrainingplan.screendrawing

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._

class DrawerTable(config: Config) {

  val defaultHzBorderChar = "-"
  val defaultVtBorderChar = "|"

  var tablesNumberPerLine: Int = config.getInt("configuration.screen-drawing.table.tables-number-per-line")
  var rowsNumberPerTable: Int = config.getInt("configuration.screen-drawing.table.rows-number-per-table")

  var headerName: Option[String] = None

  var hzBorderCharacter = defaultHzBorderChar
  var vtBorderCharacter = defaultVtBorderChar

  def withTablesNumberPerLine(value: Int): DrawerTable = {
    this.tablesNumberPerLine = value
    this
  }

  def withRowsNumberPerTable(value: Int): DrawerTable = {
    this.rowsNumberPerTable = value
    this
  }

  def withHeaderName(value: Option[String]): DrawerTable = {
    this.headerName = value
    this
  }

  def withNoBorder: DrawerTable = {
    this.hzBorderCharacter = ""
    this.vtBorderCharacter = ""
    this
  }

  def withBorder: DrawerTable = {
    this.hzBorderCharacter = defaultHzBorderChar
    this.vtBorderCharacter = defaultVtBorderChar
    this
  }

  def drawTable(columnsHeader: Seq[String], data: Seq[TableRow]*) = {
    val totalRowsPerLine = rowsNumberPerTable * tablesNumberPerLine
    var nbTables = data(0).size / totalRowsPerLine
    if (data(0).size % totalRowsPerLine != 0) {
      nbTables += 1
    }
    for(i <- 1 to nbTables) {
      val dataStartIndex = (i - 1) * totalRowsPerLine
      var rest = data(0).size - dataStartIndex
      if (rest > totalRowsPerLine) {
        rest = totalRowsPerLine
      }
      var tablesTotalNumberRealPerLine = rest / rowsNumberPerTable
      if (rest % rowsNumberPerTable != 0) {
        tablesTotalNumberRealPerLine += 1
      }
      drawDataTable(dataStartIndex, tablesTotalNumberRealPerLine, headerName.getOrElse(""), columnsHeader, data:_*)
      printLineBreak
      printLineBreak
    }
  }

  private def printHeaderName(name: String, count: Int) = {
    printSpace(1)
    if (!name.isEmpty) {
      print(name)
      printLineBreak
      printSpace(1)
    }
    printCharacter(hzBorderCharacter, count - 2)
    printSpace(1)
  }

  private def drawDataTable(dataStartIndex: Int, tablesTotalNumberRealPerLine: Int, headerLabel: String, columnsHeader: Seq[String], data: Seq[TableRow]*) = {
    var columnHeaderToLength: Map[Int, Int] = Map()
    for(i <- 0 to (columnsHeader.size - 1)) {
      val columnHeaderLength = Math.max(columnsHeader(i).length, data(i).maxBy(_.value.length).value.length)
      columnHeaderToLength += (i -> columnHeaderLength)
    }
    // display table header
    val tableWidth = columnHeaderToLength.values.sum + (columnsHeader.size * 2) + 2 + (columnsHeader.size - 1)

    for (i <- 1 to tablesTotalNumberRealPerLine) {
      printHeaderName(headerLabel, tableWidth)
      printSpace(2)
    }

    printLineBreak

    // display table header columns
    for (i <- 1 to tablesTotalNumberRealPerLine) {
      printCharacter(vtBorderCharacter, 1)
      for(columnIndex <- 0 to (columnsHeader.size - 1)) {
        val columnHeaderLength: Int = columnHeaderToLength.getOrElse(columnIndex, 0)
        printSpace(1)
        val value: String = columnsHeader(columnIndex)
        print(value)
        printSpace(1)
        printSpace(columnHeaderLength - value.length)
        if (columnIndex != columnsHeader.size - 1) {
          printPipe(1)
        }
      }
      printCharacter(vtBorderCharacter, 1)
      printSpace(2)
    }

    printLineBreak

    for (i <- 1 to tablesTotalNumberRealPerLine) {
      printSpace(1)
      printDash(tableWidth - 2)
      printSpace(3)
    }

    var tableDrawEnding = false
    // display table data values
    for(rowIndexPerTable <- 0 to (rowsNumberPerTable - 1)) {
      for (i <- 0 to (tablesTotalNumberRealPerLine -1)) {
        val rowIndex = (rowIndexPerTable + (rowsNumberPerTable * i)) + dataStartIndex
        if (rowIndex < data(0).size) {
          if (i == 0) {
            printLineBreak
          }
          printCharacter(vtBorderCharacter, 1)
          for(columnIndex <- 0 to (columnsHeader.size - 1)) {
            val columnHeaderLength: Int = columnHeaderToLength.getOrElse(columnIndex, 0)
            printSpace(1)
            val rowIndex = (rowIndexPerTable + (rowsNumberPerTable * i)) + dataStartIndex
            if (rowIndex < data(columnIndex).size) {
              val tableRow: TableRow = data(columnIndex)(rowIndex)
              tableRow.t match {
                case Some(Int) => printAndFillLeftTheVoid(tableRow, columnHeaderLength)
                case _ => printAndFillRightTheVoid(tableRow, columnHeaderLength)
              }
              printSpace(1)
              if (columnIndex != columnsHeader.size - 1) {
                printPipe(1)
              }
            }
          }
          printCharacter(vtBorderCharacter, 1)
          printSpace(2)
        } else {
          if (!tableDrawEnding && tablesTotalNumberRealPerLine != 1) {
            printCharacter(hzBorderCharacter, tableWidth)
            tableDrawEnding = true
          }
        }
      }
    }

    // display table footer
    printLineBreak
    val tablesTotalNumberFinalPerLine = if (!tableDrawEnding) tablesTotalNumberRealPerLine else tablesTotalNumberRealPerLine - 1
    for (i <- 1 to tablesTotalNumberFinalPerLine) {
      printSpace(1)
      printCharacter(hzBorderCharacter, tableWidth - 2)
      printSpace(3)
    }
  }

  private def printAndFillLeftTheVoid(tableRow: TableRow, columnHeaderLength: Int) = {
    val size = columnHeaderLength - tableRow.value.length
    if (size - 1 > 0) {
      printCharacter(tableRow.fillChar, size - 1)
      printSpace(1)
      print(tableRow.value)
    }
  }

  private def printAndFillRightTheVoid(tableRow: TableRow, columnHeaderLength: Int) = {
    val size = columnHeaderLength - tableRow.value.length
    print(tableRow.value)
    if (size - 1 > -1) {
      printSpace(1)
      printCharacter(tableRow.fillChar, size - 1)
    }
  }
}
