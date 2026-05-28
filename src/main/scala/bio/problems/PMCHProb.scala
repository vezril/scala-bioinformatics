package bio.problems

import bio.algorithms.nucleic.PerfectMatching
import bio.domain.nucleic.{PerfectMatchingProblem, RnaString}
import cats.effect.IO

object PMCHProb {

  def solve(): IO[Unit] = {

    val rna = RnaString.unsafeFrom("UUCCCGCAGCUCGACCUUACUCAAGCACUGCCCAGAAUUGCGUACCGAUUGGAUUGGGAACUCGAUGGGGGACGGCCAUG")

    val r = for {
      problem <- PerfectMatchingProblem.from(rna)
      result = PerfectMatching.count(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"${err}")
      case Right(value) => IO.println(value)
    }
  }

}
