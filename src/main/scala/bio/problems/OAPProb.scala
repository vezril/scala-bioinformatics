package bio.problems

import bio.algorithms.analysis.OverlapAlignment
import bio.domain.analysis.OverlapAlignmentProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind OAP ("Overlap Alignment") runner.
  *
  * Reads two FASTA-formatted DNA strings from `oap_data.txt`, validates them into
  * an [[OverlapAlignmentProblem]], computes one optimal overlap alignment (a suffix
  * of `s` against a prefix of `t`), and prints the formatted score and alignment
  * through `IO`. Any FASTA/validation error — or fewer than two records — yields a
  * printed error message rather than a thrown exception.
  */
object OAPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/oap_data.txt"

  def solve(): IO[Unit] =
    for {
      parsed <- FastaFileReader.read(Paths.get(DataPath))
      result = for {
        records <- parsed.left.map(_.toString)
        pair <- records match {
          case s :: t :: _ => Right((s.dna, t.dna))
          case _ =>
            Left(s"expected at least two FASTA records, got ${records.size}")
        }
        problem <- OverlapAlignmentProblem
          .from(pair._1, pair._2)
          .left
          .map(_.toString)
      } yield OverlapAlignment.align(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
