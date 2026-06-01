package bio.problems

import bio.algorithms.graph.SplitDistance
import bio.domain.graph.SplitDistanceProblem
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object SPTDProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sptd_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- lines match {
        case taxaLine +: t1Line +: t2Line +: _ =>
          val taxa = taxaLine.split("\\s+").toVector
          val result = for {
            tree1   <- NewickParser.parse(t1Line).left.map(_.toString)
            tree2   <- NewickParser.parse(t2Line).left.map(_.toString)
            problem <- SplitDistanceProblem.from(taxa, tree1, tree2).left.map(_.toString)
          } yield SplitDistance.compute(problem)
          result match {
            case Left(err)       => IO.println(err)
            case Right(distance) => IO.println(distance.toString)
          }
        case _ =>
          IO.println("expected a taxa line followed by two Newick trees")
      }
    } yield ()
}
