package bio.problems

import bio.algorithms.analysis.PDistanceMatrix
import bio.domain.analysis.DistanceMatrixProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind PDST ("Creating a Distance Matrix") runner.
  *
  * Reads the DNA records from the FASTA file `pdst_data.txt`, builds the
  * `DistanceMatrixProblem`, computes the p-distance matrix, and prints it through
  * `IO` (5-decimal values, one row per line). Any FASTA/validation error yields a
  * printed message rather than a thrown exception.
  */
object PDSTProb {

  private val DataPath =
    Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/pdst_data.txt")

  def solve(): IO[Unit] =
    for {
      records <- FastaFileReader.read(DataPath)
      _ <- records match {
        case Left(err) => IO.println(s"fasta error: $err")
        case Right(recs) =>
          DistanceMatrixProblem.from(recs.map(_.dna).toVector) match {
            case Left(err)      => IO.println(err.toString)
            case Right(problem) => IO.println(PDistanceMatrix.compute(problem).format)
          }
      }
    } yield ()
}
