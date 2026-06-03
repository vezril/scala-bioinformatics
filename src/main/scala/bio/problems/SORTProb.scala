package bio.problems

import bio.algorithms.combinatorics.ReversalSortingSearch
import bio.domain.combinatorics.{Permutation, ReversalDistanceProblem}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind SORT ("Sorting by Reversals") runner.
  *
  * Reads two permutation lines (space-separated integers) from `sort_data.txt`,
  * computes the reversal distance and a sorting reversal sequence, and prints
  * the distance followed by one reversal per line through `IO`. Any
  * parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object SORTProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sort_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solveAll(raw) match {
        case Left(err)     => IO.println(err)
        case Right(result) => IO.println(result.format)
      }
    } yield ()

  private def solveAll(raw: String): Either[String, bio.domain.combinatorics.ReversalSorting] = {
    val lines = raw.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
    lines match {
      case Vector(sourceLine, targetLine) =>
        for {
          source  <- parsePermutation(sourceLine)
          target  <- parsePermutation(targetLine)
          problem <- ReversalDistanceProblem.from(source, target).left.map(_.toString)
        } yield ReversalSortingSearch.sort(problem)
      case other => Left(s"expected two permutation lines, got ${other.size}")
    }
  }

  private def parsePermutation(line: String): Either[String, Permutation] = {
    val tokens = line.split("\\s+").filter(_.nonEmpty).toVector
    val values = tokens.flatMap(_.toIntOption)
    if (values.length != tokens.length) Left(s"non-integer token in: $line")
    else Permutation.from(values).left.map(e => s"invalid permutation '$line': $e")
  }
}
