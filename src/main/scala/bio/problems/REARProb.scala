package bio.problems

import bio.algorithms.combinatorics.ReversalDistanceSearch
import bio.domain.combinatorics.{Permutation, ReversalDistanceProblem}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind REAR ("Reversal Distance") runner.
  *
  * Reads blank-line-separated blocks from `rear_data.txt`, each block holding two
  * permutation lines (space-separated integers). Computes the reversal distance
  * for each pair and prints them space-separated on one line through `IO`. Any
  * parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object REARProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rear_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solveAll(raw) match {
        case Left(err)        => IO.println(err)
        case Right(distances) => IO.println(distances.mkString(" "))
      }
    } yield ()

  private def solveAll(raw: String): Either[String, Vector[Int]] =
    splitBlocks(raw).foldLeft[Either[String, Vector[Int]]](Right(Vector.empty)) { (acc, block) =>
      for {
        ds <- acc
        d  <- solveBlock(block)
      } yield ds :+ d
    }

  private def splitBlocks(raw: String): Vector[Vector[String]] = {
    val (completed, current) = raw.linesIterator
      .map(_.trim)
      .foldLeft((Vector.empty[Vector[String]], Vector.empty[String])) { case ((done, cur), line) =>
        if (line.isEmpty) if (cur.isEmpty) (done, cur) else (done :+ cur, Vector.empty[String])
        else (done, cur :+ line)
      }
    if (current.nonEmpty) completed :+ current else completed
  }

  private def solveBlock(lines: Vector[String]): Either[String, Int] =
    lines match {
      case Vector(sourceLine, targetLine) =>
        for {
          source  <- parsePermutation(sourceLine)
          target  <- parsePermutation(targetLine)
          problem <- ReversalDistanceProblem.from(source, target).left.map(_.toString)
        } yield ReversalDistanceSearch.distance(problem).distance
      case other => Left(s"expected a 2-line block, got ${other.size}: $other")
    }

  private def parsePermutation(line: String): Either[String, Permutation] = {
    val tokens = line.split("\\s+").filter(_.nonEmpty).toVector
    val values = tokens.flatMap(_.toIntOption)
    if (values.length != tokens.length) Left(s"non-integer token in: $line")
    else Permutation.from(values).left.map(e => s"invalid permutation '$line': $e")
  }
}
