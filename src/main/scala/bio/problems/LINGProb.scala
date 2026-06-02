package bio.problems

import bio.algorithms.analysis.LinguisticComplexityAnalysis
import bio.domain.analysis.LinguisticComplexityProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind LING ("Linguistic Complexity of a Genome") runner.
  *
  * Reads the DNA string `s` from `ling_data.txt` (a single non-empty line), validates
  * it into a [[LinguisticComplexityProblem]], computes the linguistic complexity
  * `lc(s)`, and prints it (rounded to three decimals) through `IO`. Any validation
  * error yields a printed message rather than a thrown exception.
  */
object LINGProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/ling_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        line <- raw.split("\\R").iterator.map(_.trim).find(_.nonEmpty).toRight("empty input")
        dna  <- DnaString.from(line).left.map(_.toString)
      } yield LinguisticComplexityAnalysis.compute(LinguisticComplexityProblem(dna))
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
