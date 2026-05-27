package bio.problems

import bio.algorithms.recurrence.MortalFibonacciRabbits
import bio.domain.recurrence.MortalRabbitProblem
import cats.effect.IO

object FIBDProb {

  // https://rosalind.info/problems/fibd/
  def solve(): IO[Unit] = {
    val maybePop = MortalRabbitProblem.from(83, 19)

    maybePop match {
      case Left(_) => IO.println("Hello, Bioinformatics!")
      case Right(pop) =>
        val result = MortalFibonacciRabbits.population(pop)
        IO.println(s"${result}")
    }
  }

}
