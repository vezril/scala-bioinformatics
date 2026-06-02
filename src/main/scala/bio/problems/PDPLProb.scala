package bio.problems

import bio.algorithms.combinatorics.RestrictionMapConstruction
import bio.domain.combinatorics.RestrictionMapProblem
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind PDPL ("Creating a Restriction Map") runner.
  *
  * Reads the distance multiset `L` (a single whitespace-separated line of integers)
  * from `pdpl_data.txt`, validates it into a [[RestrictionMapProblem]], reconstructs a
  * set `X` with `ΔX = L`, and prints its positions through `IO`. A parse/validation
  * error — or an unrealisable multiset — yields a printed message rather than a thrown
  * exception.
  */
object PDPLProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/pdpl_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        distances <- parseDistances(raw)
        problem   <- RestrictionMapProblem.from(distances).left.map(_.toString)
        map <- RestrictionMapConstruction
          .solve(problem)
          .toRight("no restriction map exists for the given distance multiset")
      } yield map
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(map) => IO.println(map.format)
      }
    } yield ()

  /** Parses the whitespace-separated integers of `L`. */
  private def parseDistances(raw: String): Either[String, Vector[Int]] =
    raw
      .split("\\s+")
      .iterator
      .filter(_.nonEmpty)
      .toVector
      .foldRight[Either[String, List[Int]]](Right(Nil)) { (tok, acc) =>
        for {
          rest <- acc
          n    <- tok.toIntOption.toRight(s"invalid distance '$tok'")
        } yield n :: rest
      }
      .map(_.toVector)
}
