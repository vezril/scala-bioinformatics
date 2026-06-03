package bio.problems

import bio.algorithms.analysis.MaximizeGapSymbols
import bio.domain.analysis.MaxGapProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind MGAP ("Maximizing the Gap Symbols of an Optimal Alignment") runner.
  *
  * Reads two FASTA-formatted DNA strings `s` and `t` from `mgap_data.txt`, validates
  * them into a [[MaxGapProblem]], computes the maximum number of gap symbols across all
  * maximum-score alignments, and prints it through `IO`. Any FASTA/validation error —
  * or fewer than two records — yields a printed message rather than a thrown exception.
  */
object MGAPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mgap_data.txt"

  def solve(): IO[Unit] =
    for {
      parsed <- FastaFileReader.read(Paths.get(DataPath))
      result = for {
        records <- parsed.left.map(_.toString)
        pair <- records match {
          case s :: t :: _ => Right((s.dna, t.dna))
          case _           => Left(s"expected at least two FASTA records, got ${records.size}")
        }
        problem <- MaxGapProblem.from(pair._1, pair._2).left.map(_.toString)
      } yield MaximizeGapSymbols.maxGaps(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
