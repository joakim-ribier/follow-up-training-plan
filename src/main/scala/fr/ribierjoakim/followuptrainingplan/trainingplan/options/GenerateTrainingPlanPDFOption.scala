package fr.ribierjoakim.followuptrainingplan.trainingplan.options

import com.typesafe.config.Config
import fr.ribierjoakim.followuptrainingplan.common.MyConfig._
import fr.ribierjoakim.followuptrainingplan.common.components.LoadingScreenComponent
import fr.ribierjoakim.followuptrainingplan.common.helpers.ITextHelpers
import fr.ribierjoakim.followuptrainingplan.common.services.GenerateTrainingPlanPDFService
import fr.ribierjoakim.followuptrainingplan.options.MainOption
import fr.ribierjoakim.followuptrainingplan.screendrawing.DrawerHashUtils.{printInfo, printLineBreak}
import fr.ribierjoakim.followuptrainingplan.trainingplan.services.TrainingPlanDecoderService

class GenerateTrainingPlanPDFOption(config: Config) extends MainOption(config) with ITextHelpers {

  override def start = {
    val decoderService = new TrainingPlanDecoderService(config)
    decoderService.getCurrentTrainingPlan match {
      case Some(trainingPlan) => {

        printInfo(config.getString("message.training-plan.pdf.generate-start"))
        printLineBreak

        LoadingScreenComponent.start
        val fileName = new GenerateTrainingPlanPDFService(config).process(trainingPlan)
        LoadingScreenComponent.stop
        
        println(config.getStringWithArgs("message.training-plan.pdf.file.label", fileName))
        printInfo(config.getString("message.training-plan.pdf.generate-successful"))
      }
      case _ => throw new IllegalArgumentException("An error has occurred during the decoding of the current training plan.")
    }
  }

  override def titleMenuKey = "message.training-plan.option.generate.title"
}