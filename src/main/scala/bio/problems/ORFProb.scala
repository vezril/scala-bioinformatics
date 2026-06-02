package bio.problems

import bio.algorithms.protein.OpenReadingFrames
import bio.domain.protein.OpenReadingFrameProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind ORF ("Open Reading Frames") runner.
  *
  * Reads a single FASTA-formatted DNA string from `orf_data.txt`, validates it into
  * an [[OpenReadingFrameProblem]], finds every distinct candidate protein
  * translatable from an open reading frame across the six reading frames, and prints
  * each candidate on its own line through `IO`. Any FASTA/validation error — or a
  * file with no record — yields a printed error message rather than a thrown
  * exception.
  */
object ORFProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/orf_data.txt"

  def solve(): IO[Unit] =
    for {
      parsed <- FastaFileReader.read(Paths.get(DataPath))
      result = for {
        records <- parsed.left.map(_.toString)
        dna <- records.headOption
          .map(_.dna)
          .toRight("expected at least one FASTA record in orf_data.txt")
        problem <- OpenReadingFrameProblem.from(dna).left.map(_.toString)
      } yield OpenReadingFrames.find(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
