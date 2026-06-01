package bio.problems

import bio.algorithms.graph.Quartets
import bio.domain.graph.QuartetsProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object QRTProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/qrt_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines      = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      taxa       = lines.headOption.fold(Vector.empty[String])(_.split("\\s+").toVector)
      characters = lines.drop(1)
      _ <- QuartetsProblem.from(taxa, characters) match {
        case Left(err) => IO.println(s"$err")
        case Right(problem) =>
          IO.println(Quartets.compute(problem).map(_.render).mkString("\n"))
      }
    } yield ()
}
