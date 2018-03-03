package fr.ribierjoakim.followuptrainingplan.common.components

import akka.actor.ActorSystem
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils

import scala.concurrent.duration._
import scala.language.postfixOps

object LoadingScreenComponent {

  val actorSystem = ActorSystem()
  val scheduler = actorSystem.scheduler
  implicit val executor = actorSystem.dispatcher

  val latency = 150
  val count = 3
  var inProgress = true
  var whileTrue = true

  def start = {
    whileTrue = true
    doWhile({
      inProgress = true
      printCharacterWithLatency(".", count)
      print("\r")
      DrawerHashUtils.printSpace(count * 2)
      print("\r")
      inProgress = false
    }, whileTrue, 1000)
  }

  def stop = {
    whileTrue = false
    //if (inProgress) {
      Thread.sleep((latency * count) + latency)
    //}
    print("\r")
    DrawerHashUtils.printSpace(count * 2)
    print("\r")
  }

  private def printCharacterWithLatency(value: String, size: Int) = {
    for (i <- 1 to size) {
      print(value)
      Thread.sleep(latency)
    }
  }

  private def doOnce(fn: => Unit, period:Long) = scheduler.scheduleOnce(period milliseconds)(fn)

  private def doWhile(fn: => Unit, whileFn: => Boolean, period:Long) {
    if (whileFn) {
      fn
      doOnce(doWhile(fn, whileFn, period), period)
    }
  }
}
