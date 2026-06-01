package bio.problems

import bio.algorithms.graph.PerfectCoverageAssembly
import bio.domain.graph.PerfectCoverageProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object PCOVProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/pcov_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      result = for {
        reads <- lines
          .foldRight[Either[String, List[DnaString]]](Right(Nil)) { (line, acc) =>
            for {
              rest <- acc
              dna  <- DnaString.from(line).left.map(err => s"invalid read '$line': $err")
            } yield dna :: rest
          }
          .map(_.toVector)
        problem <- PerfectCoverageProblem.from(reads).left.map(_.toString)
      } yield PerfectCoverageAssembly.assemble(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
