package bio.problems

import bio.algorithms.genetics.IndependentAlleles
import bio.domain.genetics.IndependentAllelesProblem
import cats.effect.IO

object LIAProb {

  def solve(): IO[Unit] = {

    val generations = 7
    val atLeast = 30
    IndependentAllelesProblem.from(generations, atLeast) match {
      case Left(_) => IO.println("")
      case Right(sample) =>
        val result = IndependentAlleles.probability(sample)
        IO.println(s"${result.value}")
    }
  }
}
