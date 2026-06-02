package bio.problems

import bio.algorithms.nucleic.RestrictionSites
import bio.domain.nucleic.RestrictionSiteProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind REVP ("Locating Restriction Sites") runner.
  *
  * Reads a single FASTA-formatted DNA string from `revp_data.txt`, validates it
  * into a [[RestrictionSiteProblem]], locates every reverse palindrome of length
  * 4–12, and prints each one's 1-based position and length through `IO`. Any
  * FASTA/validation error — or a file with no record — yields a printed error
  * message rather than a thrown exception.
  */
object REVPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/revp_data.txt"

  def solve(): IO[Unit] =
    for {
      parsed <- FastaFileReader.read(Paths.get(DataPath))
      result = for {
        records <- parsed.left.map(_.toString)
        dna <- records.headOption
          .map(_.dna)
          .toRight("expected at least one FASTA record in revp_data.txt")
        problem <- RestrictionSiteProblem.from(dna).left.map(_.toString)
      } yield RestrictionSites.locate(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
