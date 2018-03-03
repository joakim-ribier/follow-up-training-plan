package fr.ribierjoakim.followuptrainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.exceptions.ExitMenuException

class ExitMenuOption(config: Config) extends MainOption(config) {

  override def start = throw new ExitMenuException

  override def titleMenuKey : String = "message.option.exit-menu.title"
}
