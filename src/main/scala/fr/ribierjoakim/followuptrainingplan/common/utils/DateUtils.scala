package fr.ribierjoakim.followuptrainingplan.common.utils

import io.circe.{Decoder, Encoder}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateUtils {

  lazy val UI_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
  lazy val UI_DATE_PATTERN: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  lazy val CORE_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
  lazy val CORE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  implicit val dateTimeEncoder: Encoder[DateTime] = Encoder.encodeString.contramap[DateTime] { dateTime =>
    DateUtils.print(dateTime, CORE_DATE_TIME_FORMATTER)
  }

  implicit val dateTimeDecoder: Decoder[DateTime] = Decoder.decodeString.emap { value =>
    try {
      Right(DateUtils.parse(value, CORE_DATE_TIME_FORMATTER))
    } catch {
      case t: Throwable => Left(t.getMessage)
    }
  }

  /** Get epoch date (1970-01-01). */
  def epoch: DateTime = parse("1970-01-01T00:00:00", CORE_DATE_TIME_FORMATTER)

  def parse(value: String, formatter: DateTimeFormatter): DateTime = formatter.parseDateTime(value)

  def print(value: DateTime, formatter: DateTimeFormatter): String = formatter.print(value)
}
