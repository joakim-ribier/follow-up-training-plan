package fr.ribierjoakim.followuptrainingplan.computeaverage.models

import org.scalatest._

class PaceTimeTest extends FreeSpec {

  "to" - {
    "should convert (HH:mm:ss) string value" in {
      val paceTime: Option[PaceTime] = PaceTime.to("02:54:59")
      assert(paceTime === Some(new PaceTime(2, 54, 59)))
    }

    "should convert (mm:ss) string value" in {
      val paceTime: Option[PaceTime] = PaceTime.to("54:59")
      assert(paceTime === Some(new PaceTime(0, 54, 59)))
    }

    "should convert (ss) string value" in {
      val paceTime: Option[PaceTime] = PaceTime.to("59")
      assert(paceTime === Some(new PaceTime(0, 0, 59)))
    }

    "should throw NumberFormatException if bad format value" in {
      assertThrows[NumberFormatException] {
        PaceTime.to("aaa")
      }
    }
  }

  "toString" - {
    "should display hh(h) mm'ss" in {
      val paceTime: Option[PaceTime] = PaceTime.to("02:54:59")
      assert(paceTime.get.toString() === "2h 54'59")
    }

    "should display mm'ss" in {
      val paceTime: Option[PaceTime] = PaceTime.to("54:59")
      assert(paceTime.get.toString() === "54'59")
    }

    "should display ss" in {
      val paceTime: Option[PaceTime] = PaceTime.to("59")
      assert(paceTime.get.toString() === "59")
    }
  }
}
