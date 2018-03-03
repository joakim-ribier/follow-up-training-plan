package fr.ribierjoakim.followuptrainingplan.common.exceptions

import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._

object MyThrowable {

  implicit def throwableToMyThrowable(throwable: Throwable) = MyThrowable(throwable)
}

case class MyThrowable(throwable: Throwable) {

  def console = printError(throwable.toString)
}