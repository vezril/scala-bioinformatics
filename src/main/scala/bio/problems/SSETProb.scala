package bio.problems

import bio.algorithms.combinatorics.Subsets
import bio.domain.combinatorics.SubsetUniverseSize
import cats.effect.IO

object SSETProb {

  def solve(): IO[Unit] = {

    val n = 962

    val r = for {
      prob <- SubsetUniverseSize.from(n)
      result = Subsets.count(prob)
    } yield result

    r match {
      case Left(err) => IO.println(s"err: ${err.toString}")
      case Right(out) => IO.println(s"${out}")
    }
  }

}
