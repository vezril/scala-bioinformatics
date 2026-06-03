package bio.problems

import bio.algorithms.combinatorics.LongestSubsequences
import bio.domain.combinatorics.Permutation
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind LGIS ("Longest Increasing Subsequence") runner.
  *
  * Reads `n` (first nonblank line) and the permutation (second nonblank line,
  * space-separated) from `lgis_data.txt`, computes a longest increasing and a
  * longest decreasing subsequence, and prints them through `IO` (one per line).
  * Any parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object LGISProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lgis_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solve(raw) match {
        case Left(err)     => IO.println(err)
        case Right(result) => IO.println(result.format)
      }
    } yield ()

  private def solve(raw: String): Either[String, bio.domain.combinatorics.MonotonicSubsequences] = {
    val lines = raw.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
    lines.drop(1).headOption match {
      case None => Left("expected a permutation on the second line")
      case Some(permLine) =>
        val parsed = permLine.split("\\s+").filter(_.nonEmpty).toVector
        val values = parsed.flatMap(s => s.toIntOption)
        if (values.length != parsed.length)
          Left(s"non-integer token in permutation: $permLine")
        else
          Permutation
            .from(values)
            .left
            .map(_.toString)
            .map(LongestSubsequences.find)
    }
  }
}
