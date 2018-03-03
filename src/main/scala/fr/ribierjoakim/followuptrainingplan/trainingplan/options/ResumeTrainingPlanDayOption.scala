package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils._
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.{TrainingDay, TrainingDayType, TrainingPlan}
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService
import org.joda.time.{DateTime, Weeks}

import scala.collection.immutable.ListMap

class ResumeTrainingPlanDayOption(config: Config) extends MainOption(config) {

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(trainingPlan) => {

        printLineBreak
        println(config.getString("message.training-plan.day.type.desc"))
        printLineBreak

        val dateTime: DateTime = readDateTime(config, "message.training-plan.day.set-date.label")
        val label: String = readString(config, "message.training-plan.day.set-label.label")
        val maybeType: Option[String] = readStringOpt(config, "message.training-plan.day.set-type.label")
        val maybeKm: Option[Double] = readDoubleOpt(config, "message.training-plan.day.set-km.label")
        printLineBreak
        val comment: Option[String] = readStringOpt(config, "message.training-plan.day.set-comment.label")
        val site: Option[String] = readStringOpt(config, "message.training-plan.day.set-site.label")

        printLineBreak
        if (readBoolean(config, "message.app.confirm-to-continue")) {
          val `type` = TrainingDayType.parse(maybeType.getOrElse(""))
          val trainingPlanDay = TrainingDay(label, dateTime, `type`, comment, site, maybeKm)
          writeCurrentData(config, addNewTrainingDay(trainingPlan, trainingPlanDay))
        }
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  private def addNewTrainingDay(trainingPlan: TrainingPlan, trainingPlanDay: TrainingDay): TrainingPlan = {
    val weekNumberKey = getFormatWeekNumber(trainingPlan, trainingPlanDay)
    var trainingPlanWeeks: Map[String, Seq[TrainingDay]] = trainingPlan.weeks

    val trainingPlanDays = trainingPlanWeeks.getOrElse(weekNumberKey, Seq())
      .filterNot(x => x.dateTime.equals(trainingPlanDay.dateTime)) :+ trainingPlanDay

    trainingPlanWeeks += (weekNumberKey -> trainingPlanDays.sortWith(TrainingDay.sortByDateTime))

    trainingPlan.copy(weeks = ListMap(trainingPlanWeeks.toSeq.sortBy(_._1):_*))
  }

  private def getFormatWeekNumber(trainingPlan: TrainingPlan, trainingPlanDay: TrainingDay) = {
    val weekNumber: Int = Weeks.weeksBetween(trainingPlan.startDate, trainingPlanDay.dateTime).getWeeks() + 1;
    weekNumber.toString
  }

  override def titleMenuKey = "message.training-plan.option.add.title"
}
