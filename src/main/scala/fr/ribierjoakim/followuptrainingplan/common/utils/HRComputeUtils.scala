package fr.ribierjoakim.followuptrainingplan.common.utils

object HRComputeUtils {

  def hrReserve(hrMax: Int, hrRest: Int): Int = hrMax - hrRest

  def hrFooting(hrReserve: Int, hrRest: Int): Int = ((hrReserve * 0.7) + hrRest).toInt

  def hrMarathon(hrReserve: Int, hrRest: Int): (Int, Int) = {
    (((hrReserve * 0.8) + hrRest).toInt, ((hrReserve * 0.85) + hrRest).toInt)
  }
}