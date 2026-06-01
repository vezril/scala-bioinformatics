package bio.problems

import bio.algorithms.combinatorics.UnrootedBinaryTrees
import bio.domain.combinatorics.LeafCount
import cats.effect.IO

object CUNRProb {

  def solve(): IO[Unit] = {

    val n = 831

    val r = for {
      prob <- LeafCount.from(n)
      result = UnrootedBinaryTrees.count(prob)
    } yield result

    r match {
      case Left(err)  => IO.println(s"err: ${err.toString}")
      case Right(out) => IO.println(s"${out}")
    }
  }

}
