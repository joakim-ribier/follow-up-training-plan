package fr.ribierjoakim.followuptrainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._

class ExitOption(config: Config) extends MainOption(config) {

  override def start = {
    printLineBreak
    drawCharacters("bye bye")
    printLineBreak

    System.exit(1)
  }

  override def titleMenuKey : String = "message.option.exit.title"
}
