package fr.ribierjoakim.followuptrainingplan

import java.util.concurrent.Executors

import com.typesafe.config.{Config, ConfigFactory}
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.common.exceptions.MyThrowable._
import fr.ribierjoakim.followuptrainingplan.computeaverage.options.{ComputeAveragePaceOption, ComputeRunTimesOption}
import fr.ribierjoakim.followuptrainingplan.options._
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.options.MainTrainingPlanOption

import scala.collection.immutable.ListMap
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object Main {

  implicit val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  def main(args: Array[String]) {
    val config: Config = ConfigFactory.load("application")
    configure(config)

    val menuOptions: ListMap[String, MainOption] = ListMap(
      "1" -> new ComputeAveragePaceOption(config),
      "2" -> new ComputeRunTimesOption(config),
      "3" -> new MainTrainingPlanOption(config),
      "x" -> new ExitOption(config))

    clear()

    drawCharactersWithLatency(config.getString("message.app.title"), config.getInt("configuration.screen-drawing.display-text-latency-ms"))
    drawUnderlineFor(config.getString("message.app.title"), config.getString("message.app.copyright"))
    printLineBreak

    menu(config, menuOptions)
  }

  private def menu(config: Config, options: Map[String, MainOption]) : Unit = {
    printLineBreak
    drawCharacters(config.getString("message.app.menu"))
    printLineBreak

    options.map { x  =>
      println(s"${x._1}. ${config.getString(x._2.titleMenuKey)}")
    }
    printLineBreak
    print(config.getString("message.menu.choose-an-option"))
    try {
      val value = scala.io.StdIn.readLine().toString
      options.getOrElse(value, options.get("x").get).start
    } catch {
      case x: Throwable => x.console
    }
    printLineBreak
    println(config.getString("message.app.press-key-to-continue"))
    scala.io.StdIn.readLine()

    menu(config, options)
  }

  /**
    * Clear terminal screen
    *
    * 'H' means move to top of the screen
    * '2J' means "clear entire screen"
    */
  private def clear() = {
    print("\033[H\033[2J");
  }

  /** Sets the proxy configuration if needed... */
  private def configure(config: Config) = {
    Future {
      config.getStringOpt("configuration.http.proxyHost").map { host =>
        System.setProperty("http.proxyHost", host);
        System.setProperty("https.proxyHost", host);
      }
      config.getStringOpt("configuration.http.proxyPort").map { port =>
        System.setProperty("http.proxyPort", port);
        System.setProperty("https.proxyPort", port);
      }
    }
  }
}
