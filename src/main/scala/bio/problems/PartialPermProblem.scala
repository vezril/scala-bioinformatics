package bio.problems

import bio.algorithms.combinatorics.PartialPermutations
import bio.domain.combinatorics.PartialPermutationProblem
import cats.effect.IO

object PartialPermProblem {

  def solve(): IO[Unit] = {

    val sample = PartialPermutationProblem.from(98, 10)

    sample match {
      case Left(_) => IO.println("")
      case Right(perms) =>
        val out = PartialPermutations.count(perms)
        IO.println(s"${out}")
    }
  }
}
