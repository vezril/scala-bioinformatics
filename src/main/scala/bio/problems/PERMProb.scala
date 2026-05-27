package bio.problems

import bio.algorithms.combinatorics.Permutations
import bio.domain.combinatorics.PermutationLength
import cats.effect.IO

object PERMProb {

  def solve(): IO[Unit] = {

    PermutationLength.from(7) match {
      case Left(_) => IO.println("")
      case Right(perms) =>
        val permutations = Permutations.enumerate(perms)

        val p = permutations
          .map(v => v.fold("")((accumulator, value) => accumulator + s"${value.toString} "))
          .fold("")((accumulator, vec) => accumulator + s"${vec}\n")

        val out = s"${permutations.size}\n" + p

        IO.println(out)
    }
  }

}
