package fr.ribierjoakim.followuptrainingplan.trainingplan.models

import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingDayType.{REST, RUNNING, VAA, _}
import org.scalatest.FreeSpec

class TrainingDayTypeTest extends FreeSpec {

  "values" - {
    "should equals" in {
      assert(TrainingDayType.values.toSeq === Seq(BIKING, REST, RUNNING, SWIMMING, YOGA, VAA))
    }
  }

  "parse" - {
    "'bik' should equals BIKING" in {
      assert(TrainingDayType.parse(Some("bik")) === BIKING)
    }
    "'rest' should equals REST" in {
      assert(TrainingDayType.parse(Some("rest")) === REST)
    }
    "'run' should equals RUNNING" in {
      assert(TrainingDayType.parse(Some("run")) === RUNNING)
    }
    "'swim' should equals SWIMMING" in {
      assert(TrainingDayType.parse(Some("swim")) === SWIMMING)
    }
    "'vaa' should equals YOGA" in {
      assert(TrainingDayType.parse(Some("vaa")) === VAA)
    }
    "'yoga' should equals VAA" in {
      assert(TrainingDayType.parse(Some("yoga")) === YOGA)
    }
    "should return RUNNING by default" in {
      assert(TrainingDayType.parse(None) === RUNNING)
      assert(TrainingDayType.parse(Some("value no exists")) === RUNNING)
    }
  }
}
