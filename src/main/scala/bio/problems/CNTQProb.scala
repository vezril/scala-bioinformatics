package bio.problems

import bio.algorithms.graph.CountingQuartets
import bio.domain.graph.CountingQuartetsProblem
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scala.util.Try

object CNTQProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/cntq_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- lines match {
        case nLine +: treeLine +: _ =>
          val result = for {
            n       <- Try(nLine.toInt).toEither.left.map(_ => s"invalid leaf count: $nLine")
            tree    <- NewickParser.parse(treeLine).left.map(_.toString)
            problem <- CountingQuartetsProblem.from(n, tree).left.map(_.toString)
          } yield CountingQuartets.count(problem)
          result match {
            case Left(err)    => IO.println(err)
            case Right(count) => IO.println(count.toString)
          }
        case _ =>
          IO.println("expected a leaf count followed by a Newick tree")
      }
    } yield ()
}
