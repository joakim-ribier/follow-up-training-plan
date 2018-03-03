package fr.ribierjoakim.followuptrainingplan.computeaverage.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.utils.{PaceComputeUtils, TimeStringConverterUtils}
import fr.ribierjoakim.followuptrainingplan.computeaverage.models.PaceTime
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._

class ComputeAveragePaceOption(config: Config) extends MainOption(config) {

  override def start = {
    printLineBreak
    val distance = displayAndReadDistanceFromInputLine(config)
    val time = readString(config, "message.compute-run-times.time.label")

    TimeStringConverterUtils.convertToSeconds(time).map { x =>
      val paceTime: PaceTime = PaceComputeUtils.pacePerMinutes(distance, x)
      val hoursPerKm = PaceComputeUtils.pacePerHours(distance, x)

      printInfo(config.getStringWithArgs("message.compute-average-pace.pace.result", paceTime, hoursPerKm))
    }
  }

  override def titleMenuKey : String = "message.option.compute-average-pace.title"
}
