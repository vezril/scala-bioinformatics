package bio.problems

import bio.algorithms.combinatorics.VaryingLengthLexOrder
import bio.domain.combinatorics.LexOrderProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind LEXV ("Ordering Strings of Varying Length Lexicographically") runner.
  *
  * Reads the ordered alphabet (line 1, space-separated symbols) and the maximum
  * length `n` (line 2) from `lexv_data.txt`, enumerates every string of length 1
  * to `n` in varying-length lexicographic order, and prints them one per line
  * through `IO`. Any parse/validation error yields a printed message rather than
  * a thrown exception.
  */
object LEXVProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lexv_data.txt"

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

  private def solve(raw: String): Either[String, bio.domain.combinatorics.LexOrdering] = {
    val lines = raw.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
    lines match {
      case Vector(alphabetLine, lengthLine) =>
        val alphabet = alphabetLine.split("\\s+").filter(_.nonEmpty).map(_.head).toVector
        lengthLine.toIntOption match {
          case None => Left(s"invalid length: $lengthLine")
          case Some(n) =>
            LexOrderProblem
              .from(alphabet, n)
              .left
              .map(_.toString)
              .map(VaryingLengthLexOrder.enumerate)
        }
      case other => Left(s"expected an alphabet line and a length line, got ${other.size} lines")
    }
  }
}
