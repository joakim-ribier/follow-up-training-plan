package fr.ribierjoakim.followuptrainingplan.common

object MyString {

  implicit def stringToMyString(value: String) = MyString(value)
}

case class MyString(value: String) {

  def displayFromJson: String = {
    value.replace("\\n", "\n").replace("\\r", "\r")
  }
}