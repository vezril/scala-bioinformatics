package bio.problems

import bio.algorithms.combinatorics.Combinations
import bio.domain.combinatorics.CombinationSumProblem
import cats.effect.IO

object ASPCProb {

  def solve(): IO[Unit] = {

    val n = 1901
    val k = 603

    val result = for {
      prob <- CombinationSumProblem.from(n, k)
      c = Combinations.sumFrom(prob)
    } yield c

    result match {
      case Left(err) => IO.println(s"err: ${err}")
      case Right(r) => IO.println(s"${r}")
    }
  }
}
