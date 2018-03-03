package fr.ribierjoakim.followuptrainingplan.screendrawing

object TableRow {

  val fillWithDotChar = "."
  val fillWithEmptyChar = " "
}

class TableRow(val value: String, val t: Option[Any], val fillChar: String = TableRow.fillWithDotChar) {
}
