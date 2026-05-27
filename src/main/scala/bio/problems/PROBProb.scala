package bio.problems

import bio.algorithms.analysis.RandomMatch
import bio.domain.analysis.RandomMatchProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import cats.effect.IO

object PROBProb {

  def solve(): IO[Unit] = {

    val dnaString = DnaString.unsafeFrom("TGTGGGACCTACACGCTAGGAACCTAACAACGGCAGTAGTCGGCTAGCCCATCTAACAATCTGAATTTCGGTTCATCATTATCACGT")
    val gcContent = "0.088 0.137 0.188 0.239 0.307 0.371 0.416 0.503 0.587 0.616 0.689 0.706 0.770 0.837 0.921"
      .split(" ")
      .map(_.toDouble)
      .map(Probability.unsafeFrom)
      .toVector

    val result = for {
      input <- RandomMatchProblem.from(dnaString, gcContent)
      out = RandomMatch.logProbabilities(input)
    } yield out

    def format(n: Double): String = {
      BigDecimal(n)
        .setScale(3, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
        .toString
    }

    result match {
      case Left(_) => IO.println("")
      case Right(probs) =>
        IO.println(s"${probs.foldLeft("")((acc, p) => acc ++ s"${format(p)} ")}")
    }
  }
}
