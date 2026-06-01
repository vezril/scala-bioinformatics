package bio.problems

import bio.algorithms.combinatorics.SetOperations
import bio.domain.combinatorics.SetOperationsProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scala.util.Try

object SETOProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/seto_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- lines match {
        case nLine +: aLine +: bLine +: _ =>
          val result = for {
            n       <- Try(nLine.toInt).toEither.left.map(_ => s"invalid universe size: $nLine")
            a       <- parseSet(aLine)
            b       <- parseSet(bLine)
            problem <- SetOperationsProblem.from(n, a, b).left.map(_.toString)
          } yield SetOperations.compute(problem)
          result match {
            case Left(err)  => IO.println(err)
            case Right(res) => IO.println(res.format)
          }
        case _ =>
          IO.println("expected a universe size followed by two subsets")
      }
    } yield ()

  /** Parses a brace-delimited set line such as `{1, 2, 3}` (and `{}`) into a `Set[Int]`. */
  private def parseSet(line: String): Either[String, Set[Int]] = {
    val inner = line.stripPrefix("{").stripSuffix("}").trim
    if (inner.isEmpty) Right(Set.empty)
    else
      inner
        .split(",")
        .iterator
        .map(_.trim)
        .toVector
        .traverseToSet(token =>
          Try(token.toInt).toEither.left.map(_ => s"invalid set element: $token")
        )
  }

  private implicit class TraverseOps(private val tokens: Vector[String]) extends AnyVal {
    def traverseToSet(f: String => Either[String, Int]): Either[String, Set[Int]] =
      tokens.foldLeft[Either[String, Set[Int]]](Right(Set.empty)) { (acc, token) =>
        for {
          set     <- acc
          element <- f(token)
        } yield set + element
      }
  }
}
