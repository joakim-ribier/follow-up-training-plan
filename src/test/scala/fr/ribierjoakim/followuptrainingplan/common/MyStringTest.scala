package fr.ribierjoakim.followuptrainingplan.common

import fr.ribierjoakim.followuptrainingplan.common.MyString._
import org.scalatest.{BeforeAndAfter, FreeSpec}

class MyStringTest extends FreeSpec with BeforeAndAfter {

  "implicit def displayFromJson" - {

    "should remove character escape" in {
      val value = "first line\\n\\rsecond line"

      assert(value.displayFromJson === "first line\n\rsecond line")
    }
  }
}
