package bio.problems

import bio.algorithms.combinatorics.SignedPermutationEnumeration
import bio.domain.combinatorics.SignedPermutationProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind SIGN ("Enumerating Oriented Gene Orderings") runner.
  *
  * Reads the length `n` from `sign_data.txt`, enumerates every signed permutation
  * of length `n`, and prints the total count followed by each permutation on its
  * own line through `IO`. Any parse/validation error yields a printed message
  * rather than a thrown exception.
  */
object SIGNProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sign_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solve(raw.trim) match {
        case Left(err)     => IO.println(err)
        case Right(result) => IO.println(result.format)
      }
    } yield ()

  private def solve(raw: String): Either[String, bio.domain.combinatorics.SignedPermutations] =
    raw.toIntOption match {
      case None => Left(s"invalid length: $raw")
      case Some(n) =>
        SignedPermutationProblem
          .from(n)
          .left
          .map(_.toString)
          .map(SignedPermutationEnumeration.enumerate)
    }
}
