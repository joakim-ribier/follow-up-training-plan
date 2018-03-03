package fr.ribierjoakim.followuptrainingplan.screendrawing

object DrawerHashUtils {

  val a = Seq(" ## ", "#  #", "####", "#  #", "#  #")
  val b = Seq("### ", "#  #", "### ", "#  #", "### ")
  val c = Seq(" ###", "#   ", "#   ", "#   ", " ###")
  val e = Seq("####", "#   ", "### ", "#   ", "####")
  val f = Seq("####", "#   ", "### ", "#   ", "#   ")
  val g = Seq(" ###", "#   ", "# ##", "#  #", " ###")
  val i = Seq(" # ", " # ", " # ", " # ", " # ")
  val j = Seq("   #", "   #", "   #", "#  #", " ## ")
  val l = Seq("#  ", "#  ", "#  ", "#  ", "###")
  val m = Seq("#   #", "## ##", "# # #", "#   #", "#   #")
  val n = Seq("#  #", "## #", "# ##", "#  #", "#  #")
  val o = Seq(" ## ", "#  #", "#  #", "#  #", " ## ")
  val p = Seq("### ", "#  #", "### ", "#   ", "#   ")
  val r = Seq("### ", "#  #", "### ", "# # ", "#  #")
  val t = Seq("###", " # ", " # ", " # ", " # ")
  val u = Seq("#  #", "#  #", "#  #", "#  #", "####")
  val v = Seq("#  #", "#  #", "#  #", "#  #", " ## ")
  val w = Seq("#   #", "#   #", "#   #", "# # #", " # # ")
  val y = Seq("# #", "# #", "###", " # ", " # ")

  val dash = Seq("    ", "    ", "####", "    ", "    ")
  val dot = Seq(" ", " ", " ", " ", "#")
  val space = Seq("  ", "  ", "  ", "  ", "  ")
  val exclamation = Seq("#", "#", "#", " ", "#")
  val ??? = Seq("", "", "", "", "")

  val charHeight = a.size

  val map: Map[String, Seq[String]] = Map(
    "a" -> a, "b" -> b, "c" -> c, "e" -> e, "f" -> f, "g" -> g, "i" -> i, "j" -> j, "l" -> l,
    "m" -> m, "n" -> n, "o" -> o, "p" -> p, "u" -> u, "r" -> r, "t" -> t, "v" -> v, "w" -> w, "y" -> y,
    " " -> space, "-" -> dash, "." -> dot, "!" -> exclamation)

  def drawCharactersWithLatency(word: String, latency: Int) = {
    for (i <- 0 to charHeight - 1) {
      word.foreach { x =>
        Thread.sleep(latency)
        drawCharacter(x, i)
      }
      printLineBreak
    }
  }

  def drawCharacters(word: String) = drawCharactersWithLatency(word, 0)

  private def drawCharacter(c: Char, i: Int) = {
    map.get(c.toString).map { seq =>
      print(seq(i))
      print("  ")
    }
  }

  def drawUnderlineFor(word: String, prefixToDisplay: String) = {
    val value = prefixToDisplay + " "
    val sum = word.map(computeLength(_)).sum - value.length
    print(value)
    printTilde(sum)
  }

  private def computeLength(c: Char) : Int = {
    c.toString match {
      case " " => 2
      case _ => map.getOrElse(c.toString, Seq(" "))(0).length + 2
    }
  }

  def printLineBreak = print("\n\r")
  def printHash(count: Int) = printCharacter("#", count)
  def printDash(count: Int) = printCharacter("-", count)
  def printSpace(count: Int) = printCharacter(" ", count)
  def printTilde(count: Int) = printCharacter("~", count)
  def printDot(count: Int) = printCharacter(".", count)
  def printPipe(count: Int) = printCharacter("|", count)
  def printStar(count: Int) = printCharacter("*", count)
  def printBackslash(count: Int) = printCharacter("\\", count)
  def printSlash(count: Int) = printCharacter("/", count)

  def printInfo(value: String) = {
    printLineBreak; printDash(value.length); printLineBreak
    print(value)
    printLineBreak; printDash(value.length); printLineBreak
  }

  def printTitle(value: String) = {
    printLineBreak; printStar(value.length); printLineBreak
    print(value)
    printLineBreak; printStar(value.length); printLineBreak
  }

  def printError(value: String) = {
    printLineBreak; printBackslash(value.length); printLineBreak
    print(value)
    printLineBreak; printSlash(value.length); printLineBreak
  }

  def printCharacter(value: String, size: Int) = {
    for (i <- 1 to size) {
      print(value)
    }
  }
}
