package fr.ribierjoakim.followuptrainingplan.common

import com.typesafe.config.{Config, ConfigFactory}
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig
import fr.ribierjoakim.followuptrainingplan.trainingplan.models.TrainingDayType._
import org.scalatest.{BeforeAndAfter, FreeSpec}

class MyConfigTest extends FreeSpec with BeforeAndAfter {

  var config: Config = _
  before {
    config = ConfigFactory.load("application")
  }

  "training plan" - {

    "get training day key shortcut" - {
      "'v' should equals 'bik'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("v") === Some("bik"))
      }
      "'repos' should equals 'rest'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("repos") === Some("rest"))
      }
      "'cap' should equals 'run'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("cap") === Some("run"))
      }
      "'n' should equals 'swim'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("n") === Some("swim"))
      }
      "'vaa' should equals 'vaa'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("vaa") === Some("vaa"))
      }
      "'y' should equals 'yoga'" in {
        assert(config.TrainingPlan.getTrainingDayKeyByShortcut("y") === Some("yoga"))
      }
    }

    "get display training type value" - {
      "BIKING should equals 'vélo'" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(BIKING) === "vélo")
      }
      "REST should equals 'repos'" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(REST) === "repos")
      }
      "RUNNING should equals 'course'" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(RUNNING) === "course")
      }
      "SWIMMING should equals 'natation'" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(SWIMMING) === "natation")
      }
      "VAA should equals \"va'a'\"" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(VAA) === "va'a")
      }
      "YOGA should equals 'yoga'" in {
        assert(config.TrainingPlan.getDisplayTrainingTypeValue(YOGA) === "yoga")
      }
    }
  }
}
