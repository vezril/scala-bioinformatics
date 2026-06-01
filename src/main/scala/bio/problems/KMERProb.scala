package bio.problems

import bio.algorithms.combinatorics.ComposeKmers
import bio.domain.combinatorics.KmerCompositionProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Path

object KMERProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/kmer_data.txt"

  /** Rosalind KMER: print the 4-mer composition of the single FASTA DNA record. */
  private val K = 4

  def solve(): IO[Unit] =
    for {
      readResult <- FastaFileReader.read(Path.of(DataPath))
      _ <- readResult match {
        case Left(err) =>
          IO.println(s"FASTA read error: $err")
        case Right(records) =>
          records match {
            case record :: _ =>
              KmerCompositionProblem.from(record.dna, K) match {
                case Left(err)      => IO.println(s"Problem-construction error: $err")
                case Right(problem) => IO.println(ComposeKmers.compose(problem).format)
              }
            case Nil =>
              IO.println("Error: no FASTA records in input")
          }
      }
    } yield ()
}
