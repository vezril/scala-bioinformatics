package bio.problems

import bio.algorithms.nucleic.TransitionTransversionAnalysis
import bio.domain.nucleic.TransitionTransversionProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

/** Rosalind TRAN ("Transitions and Transversions") runner.
  *
  * Reads the two DNA records from the FASTA file `tran_data.txt`, builds the
  * `TransitionTransversionProblem`, computes the transition/transversion ratio,
  * and prints it through `IO`. Any FASTA/validation error yields a printed
  * message rather than a thrown exception.
  */
object TRANProb {

  private val DataPath =
    Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/tran_data.txt")

  def solve(): IO[Unit] =
    for {
      records <- FastaFileReader.read(DataPath)
      _ <- records match {
        case Left(err) => IO.println(s"fasta error: $err")
        case Right(recs) =>
          recs.map(_.dna) match {
            case first :: second :: _ =>
              TransitionTransversionProblem.from(first, second) match {
                case Left(err) => IO.println(err.toString)
                case Right(problem) =>
                  IO.println(TransitionTransversionAnalysis.analyze(problem).format)
              }
            case _ => IO.println(s"expected two FASTA records, got ${recs.size}")
          }
      }
    } yield ()
}
