package fr.ribierjoakim.followuptrainingplan.trainingplan.models

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.joda.time.DateTime

case class TrainingDay(
  label: String, dateTime: DateTime, `type`: TrainingDayType.Value = TrainingDayType.RUNNING,
  comment: Option[String] = None, site: Option[String] = None, km: Option[Double] = None)

object TrainingDay {
  import fr.ribierjoakim.followuptrainingplan.common.utils.DateUtils._

  implicit val trainingPlanDayEncoder: Encoder[TrainingDay] = deriveEncoder[TrainingDay]
  implicit val trainingPlanDayDecoder: Decoder[TrainingDay] = deriveDecoder[TrainingDay]

  def sortByDateTime(s1: TrainingDay, s2: TrainingDay): Boolean = {
    s1.dateTime.isBefore(s2.dateTime)
  }
}

object TrainingDayType extends Enumeration {
  type TrainingDayType = Value
  val RUNNING, SWIMMING, BIKING, REST, YOGA = Value

  val keyMapType = Map("r" -> RUNNING, "s" -> SWIMMING, "b" -> BIKING, "rest" -> REST, "y" -> YOGA)
  def parse(value: String): TrainingDayType.Value = keyMapType.getOrElse(value, RUNNING)

  implicit val trainingDayTypeEncoder: Encoder[TrainingDayType] = Encoder.encodeString.contramap[TrainingDayType](_.toString.toLowerCase)

  implicit val trainingDayTypeDecoder: Decoder[TrainingDayType] = Decoder.decodeString.emap { value =>
    try {
      Right(TrainingDayType.Value(value.toUpperCase))
    } catch {
      case t: Throwable => Left(t.getMessage)
    }
  }
}

