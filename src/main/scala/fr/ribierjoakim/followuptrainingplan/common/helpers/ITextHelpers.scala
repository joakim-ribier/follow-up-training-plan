package fr.ribierjoakim.followuptrainingplan.common.helpers

import com.itextpdf.text._
import com.itextpdf.text.pdf.PdfPCell

trait ITextHelpers {

  val lightBlue = new BaseColor(224,242,241)
  val blue = new BaseColor(57,121,107)
  val defaultFont = FontFactory.COURIER
  val titleSizeFont = 16
  
  def getCell(
    value: String, bckColor: Boolean = false, bold: Boolean = false,
    size: Int = 11, hzAlign: Int = Element.ALIGN_LEFT, padding: Float = 2.0f, color: BaseColor = BaseColor.BLACK,
    anchorRef: Option[String] = None, anchorTarget: Option[String] = None) = {

    buildCell(
      value,
      bckColor = bckColor,
      bold = bold,
      size = size,
      hzAlign = hzAlign,
      padding = padding,
      color = color,
      anchorRef = anchorRef,
      anchorTarget = anchorTarget)
  }

  def getTitlePageCell(value: String, anchorTarget: Option[String] = None) = {
    getCell(
      value.toUpperCase, size = titleSizeFont, hzAlign = Element.ALIGN_CENTER,
      color = blue, bold = true, anchorTarget = anchorTarget)
  }

  private def buildCell(value: String, bckColor: Boolean = false, bold: Boolean = false, size: Int = 11, hzAlign: Int = Element.ALIGN_LEFT,
     padding: Float = 2.0f, color: BaseColor = BaseColor.BLACK,
     anchorRef: Option[String] = None, anchorTarget: Option[String] = None) = {

    val paragraph = getParagraph(
      value, bold = bold, size = size, color = color,
      anchorRef = anchorRef, anchorTarget = anchorTarget)

    val cell = new PdfPCell(paragraph)
    cell.setBorderColor(BaseColor.WHITE)
    cell.setBorderWidth(2.0f)
    cell.setHorizontalAlignment(hzAlign)
    cell.setPadding(padding)
    cell.setUseBorderPadding(true)
    if (bckColor) {
      cell.setBackgroundColor(lightBlue)
    }
    cell
  }

  private def getParagraph(
    value: String,
    bold: Boolean = false, size: Int = 11, color: BaseColor = BaseColor.BLACK,
    anchorRef: Option[String] = None, anchorTarget: Option[String] = None): Paragraph = {

    val font = FontFactory.getFont(defaultFont, size, color)
    if (bold) {
      font.setStyle(Font.BOLD)
    }
    var paragraph = new Paragraph(value, font)
    anchorRef.map { ref =>
      val anchor = new Anchor(value, font)
      anchor.setReference(ref)
      paragraph = new Paragraph()
      paragraph.add(anchor)
    }
    anchorTarget.map { target =>
      val anchor = new Anchor(value, font)
      anchor.setName(target)
      paragraph = new Paragraph()
      paragraph.add(anchor)
    }
    paragraph
  }
}
