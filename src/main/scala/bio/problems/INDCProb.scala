package bio.problems

import bio.algorithms.genetics.IndependentSegregation
import bio.domain.genetics.ChromosomePairs
import cats.effect.IO

object INDCProb {

  def solve(): IO[Unit] = {

    val n = 44

    def format(n: Double): String = {
      BigDecimal(n)
        .setScale(3, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
        .toString
    }

    val result = for {
      prob <- ChromosomePairs.from(n)
      probs = IndependentSegregation.logProbs(prob)
        .map { p => format(p) }
    } yield probs

    result match {
      case Left(err) => IO.println(s"${err}")
      case Right(r) =>
        val out = r.fold("")((acc, p) => acc ++ s"${p} ")
        IO.println(out)
    }
  }

}
