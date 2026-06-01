package bio.problems

import bio.algorithms.graph.FixInconsistentCharacterSet
import bio.domain.graph.InconsistentCharacterSetProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object CSETProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/cset_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      rows = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- InconsistentCharacterSetProblem.from(rows) match {
        case Left(err) => IO.println(err.toString)
        case Right(problem) =>
          FixInconsistentCharacterSet.fix(problem) match {
            case Some(table) => IO.println(table.format)
            case None        => IO.println("no single-row deletion makes the table consistent")
          }
      }
    } yield ()
}
