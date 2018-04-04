package fr.ribierjoakim.followuptrainingplan.trainingplan.models

import io.circe.generic.semiauto._
import io.circe.{Decoder, _}
import org.joda.time.DateTime

case class TrainingPlan(
  name: String, startDate: DateTime, expectedTime: String,
  goalAchieved: Boolean = false, resultTime: Option[String] = None, resultComment: Option[String] = None,
  `type`: TrainingDayType.Value = TrainingDayType.RUNNING,
  plan: Option[String] = None, comment: Option[String] = None, hrMax: Option[Int] = None, hrRest: Option[Int] = None,
  weeks: Map[String, Seq[TrainingDay]] = Map(), drive: Option[Map[String, String]] = None)  {

  def formatNameToFileName = name.toLowerCase.replaceAll(" ", "-")
}

object TrainingPlan {
  import fr.ribierjoakim.followuptrainingplan.common.utils.DateUtils._

  implicit val trainingPlanEncoder: Encoder[TrainingPlan] = deriveEncoder[TrainingPlan]
  implicit val trainingPlanDecoder: Decoder[TrainingPlan] = deriveDecoder[TrainingPlan]
}
