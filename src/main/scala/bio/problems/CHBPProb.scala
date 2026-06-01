package bio.problems

import bio.algorithms.graph.CharacterBasedPhylogeny
import bio.domain.graph.CharacterBasedPhylogenyProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object CHBPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/chbp_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- lines match {
        case taxaLine +: rows =>
          val taxa = taxaLine.split("\\s+").iterator.filter(_.nonEmpty).toVector
          CharacterBasedPhylogenyProblem.from(taxa, rows) match {
            case Left(err)   => IO.println(err.toString)
            case Right(prob) => IO.println(CharacterBasedPhylogeny.build(prob).render)
          }
        case _ =>
          IO.println("expected a taxa line followed by character rows")
      }
    } yield ()
}
