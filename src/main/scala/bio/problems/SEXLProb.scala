package bio.problems

import bio.algorithms.genetics.SexLinkedInheritance
import bio.domain.genetics.SexLinkedProblem
import bio.domain.stats.Probability
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind SEXL ("Sex-Linked Inheritance") runner.
  *
  * Reads the male trait proportions (a single whitespace-separated line) from
  * `sexl_data.txt`, validates each into a [[Probability]], builds a
  * [[SexLinkedProblem]], computes the per-gene female carrier probabilities, and prints
  * them through `IO`. Any parse/validation error yields a printed message rather than a
  * thrown exception.
  */
object SEXLProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sexl_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        proportions <- parseProportions(raw)
      } yield SexLinkedInheritance.carrierProbabilities(SexLinkedProblem(proportions))
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses and validates each whitespace-separated proportion into a `Probability`. */
  private def parseProportions(raw: String): Either[String, Vector[Probability]] =
    raw
      .split("\\s+")
      .iterator
      .filter(_.nonEmpty)
      .toVector
      .foldRight[Either[String, List[Probability]]](Right(Nil)) { (tok, acc) =>
        for {
          rest <- acc
          d    <- tok.toDoubleOption.toRight(s"invalid proportion '$tok'")
          p    <- Probability.from(d).left.map(_.toString)
        } yield p :: rest
      }
      .map(_.toVector)
}
