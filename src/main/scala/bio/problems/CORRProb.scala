package bio.problems

import bio.algorithms.analysis.ReadErrorCorrection
import bio.domain.analysis.ReadCorrectionProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind CORR ("Error Correction in Reads") runner.
  *
  * Reads the DNA records from the FASTA file `corr_data.txt`, builds the
  * `ReadCorrectionProblem`, computes the single-symbol corrections, and prints
  * each `old->new` correction on its own line through `IO`. Any FASTA/validation
  * error yields a printed message rather than a thrown exception.
  */
object CORRProb {

  private val DataPath =
    Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/corr_data.txt")

  def solve(): IO[Unit] =
    for {
      records <- FastaFileReader.read(DataPath)
      _ <- records match {
        case Left(err) => IO.println(s"fasta error: $err")
        case Right(recs) =>
          ReadCorrectionProblem.from(recs.map(_.dna).toVector) match {
            case Left(err)      => IO.println(err.toString)
            case Right(problem) => IO.println(ReadErrorCorrection.correct(problem).format)
          }
      }
    } yield ()
}
