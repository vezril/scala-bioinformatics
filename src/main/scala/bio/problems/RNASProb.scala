package bio.problems

import bio.algorithms.nucleic.WobbleMatching
import bio.domain.nucleic.{RnaString, WobbleMatchingProblem}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind RNAS ("Wobble Bonding and RNA Secondary Structures") runner.
  *
  * Reads a single RNA string from `rnas_data.txt`, counts every valid
  * noncrossing matching (wobble `U`–`G` pairing allowed, `k >= i + 4` minimum
  * separation, exact `BigInt`), and prints the count through `IO`. Any
  * parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object RNASProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rnas_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solveString(raw.trim) match {
        case Left(err)     => IO.println(err)
        case Right(result) => IO.println(result.format)
      }
    } yield ()

  private def solveString(s: String): Either[String, bio.domain.nucleic.WobbleMatchings] =
    for {
      rna     <- RnaString.from(s).left.map(e => s"invalid RNA string: $e")
      problem <- WobbleMatchingProblem.from(rna).left.map(_.toString)
    } yield WobbleMatching.count(problem)
}
