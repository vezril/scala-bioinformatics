package bio.problems

import bio.algorithms.protein.RnaSplicing
import bio.domain.protein.RnaSplicingProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Path

object SPLCProb {

  def solve(): IO[Unit] = {
    val path = Path.of(
      "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rna_splicing.txt"
    )

    for {
      readResult <- FastaFileReader.read(path)
      _ <- readResult match {
        case Left(err) =>
          IO.println(s"FASTA read error: $err")

        case Right(records) =>
          // Rosalind SPLC convention: the first FASTA record is the source DNA;
          // every subsequent record is an intron.
          records match {
            case Nil =>
              IO.println("Error: no FASTA records in input")

            case sourceRecord :: intronRecords =>
              val source  = sourceRecord.dna
              val introns = intronRecords.map(_.dna).toVector

              RnaSplicingProblem.from(source, introns) match {
                case Left(err) =>
                  IO.println(s"Problem-construction error: $err")
                case Right(problem) =>
                  RnaSplicing.transcribeAndTranslate(problem) match {
                    case Left(err)      => IO.println(s"Translation error: $err")
                    case Right(protein) => IO.println(protein.value)
                  }
              }
          }
      }
    } yield ()
  }
}
