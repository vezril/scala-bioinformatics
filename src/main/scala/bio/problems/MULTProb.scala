package bio.problems

import bio.algorithms.analysis.MultipleAlignment
import bio.domain.analysis.MultipleAlignmentProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

object MULTProb {

  def solve(): IO[Unit] = {

    val strings = Vector(
      DnaString.from("CTCTGCCG".replace("\n", "")).getOrElse(sys.error("invalid DNA string 0")),
      DnaString.from("TTGAATGT".replace("\n", "")).getOrElse(sys.error("invalid DNA string 1")),
      DnaString.from("CATCGATT".replace("\n", "")).getOrElse(sys.error("invalid DNA string 2")),
      DnaString.from("AGTGAGCA".replace("\n", "")).getOrElse(sys.error("invalid DNA string 3"))
    )

    val r = for {
      problem <- MultipleAlignmentProblem.from(strings)
      result = MultipleAlignment.align(problem)
    } yield result

    r match {
      case Left(err) =>
        IO.println(s"$err")
      case Right(alignment) =>
        IO.println(alignment.score) *>
          alignment.augmentedStrings.foldLeft(IO.unit) { (acc, row) =>
            acc *> IO.println(row)
          }
    }
  }
}
