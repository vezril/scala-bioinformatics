package bio.problems

import bio.algorithms.graph.PhylogeneticAncestors
import bio.domain.graph.UnrootedBinaryTreeLeafCount
import cats.effect.IO

object INODProb {

  def solve(): IO[Unit] = {

    val n = 2918

    val r = for {
      problem <- UnrootedBinaryTreeLeafCount.from(n)
      result = PhylogeneticAncestors.internalNodes(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"${err}")
      case Right(value) => IO.println(s"${value}")
    }
  }
}
