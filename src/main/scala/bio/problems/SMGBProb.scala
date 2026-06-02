package bio.problems

import bio.algorithms.analysis.SemiglobalAlignment
import bio.domain.analysis.SemiglobalAlignmentProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind SMGB ("Semiglobal Alignment") runner.
  *
  * Reads two FASTA-formatted DNA strings from `smgb_data.txt`, validates them
  * into a [[SemiglobalAlignmentProblem]], computes one optimal semiglobal
  * alignment (all of `s` against all of `t` with free leading/trailing gaps on
  * either string), and prints the formatted score and alignment through `IO`.
  * Any FASTA/validation error — or fewer than two records — yields a printed
  * error message rather than a thrown exception.
  */
object SMGBProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/smgb_data.txt"

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
        problem <- SemiglobalAlignmentProblem
          .from(pair._1, pair._2)
          .left
          .map(_.toString)
      } yield SemiglobalAlignment.align(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
