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
  val BIKING, REST, RUNNING, SWIMMING, YOGA, VAA = Value

  val keyMapType = Map(
    "bik" -> BIKING,
    "rest" -> REST,
    "run" -> RUNNING,
    "swim" -> SWIMMING,
    "vaa" -> VAA,
    "yoga" -> YOGA)

  def parse(value: Option[String]): TrainingDayType.Value = {
    value match {
      case Some(x) => keyMapType.getOrElse(x, RUNNING)
      case _ => RUNNING
    }
  }

  implicit val trainingDayTypeEncoder: Encoder[TrainingDayType] = Encoder.encodeString.contramap[TrainingDayType](_.toString.toLowerCase)

  implicit val trainingDayTypeDecoder: Decoder[TrainingDayType] = Decoder.decodeString.emap { value =>
    try {
      Right(TrainingDayType.Value(value.toUpperCase))
    } catch {
      case t: Throwable => Left(t.getMessage)
    }
  }
}

