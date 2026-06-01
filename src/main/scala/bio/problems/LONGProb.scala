package bio.problems

import bio.algorithms.graph.GenomeAssembly
import bio.domain.graph.GenomeAssemblyProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths

object LONGProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/long_data.txt"

  def solve(): IO[Unit] =
    for {
      parsed <- FastaFileReader.read(Paths.get(DataPath))
      _ <- parsed match {
        case Left(err) => IO.println(err.toString)
        case Right(records) =>
          val reads = records.map(_.dna).toVector
          GenomeAssemblyProblem.from(reads) match {
            case Left(err) => IO.println(err.toString)
            case Right(problem) =>
              GenomeAssembly.assemble(problem) match {
                case Some(superstring) => IO.println(superstring.format)
                case None              => IO.println("no unique assembly exists for these reads")
              }
          }
      }
    } yield ()
}
