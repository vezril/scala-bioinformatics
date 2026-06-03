package bio.problems

import bio.algorithms.nucleic.MaximumMatching
import bio.domain.nucleic.{MaximumMatchingProblem, RnaString}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind MMCH ("Maximum Matchings and RNA Secondary Structures") runner.
  *
  * Reads a FASTA-formatted RNA string from `mmch_data.txt` (header line(s)
  * starting with `>` are skipped; the remaining lines are concatenated),
  * computes the total number of maximum matchings of basepair edges, and prints
  * the count through `IO`. Any parse/validation error yields a printed message
  * rather than a thrown exception.
  */
object MMCHProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mmch_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solveString(parseFasta(raw)) match {
        case Left(err)     => IO.println(err)
        case Right(result) => IO.println(result.format)
      }
    } yield ()

  /** Concatenate the non-header lines of a single-record FASTA string. */
  private def parseFasta(raw: String): String =
    raw.linesIterator.map(_.trim).filterNot(l => l.isEmpty || l.startsWith(">")).mkString

  private def solveString(s: String): Either[String, bio.domain.nucleic.MaximumMatchings] =
    for {
      rna     <- RnaString.from(s).left.map(e => s"invalid RNA string: $e")
      problem <- MaximumMatchingProblem.from(rna).left.map(_.toString)
    } yield MaximumMatching.count(problem)
}
