package bio.problems

import bio.algorithms.analysis.ExpectedRestrictionSites
import bio.domain.analysis.ExpectedRestrictionSitesProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind EVAL ("Expected Number of Restriction Sites") runner.
  *
  * Reads three lines from `eval_data.txt` — the string length `n`, the motif `s`,
  * and a whitespace-separated array of GC-contents `A` — then prints the expected
  * number of occurrences of `s` in a random length-`n` string for each GC-content.
  * Any parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object EVALProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/eval_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        parsed <- parse(raw)
        (n, motif, gcs) = parsed
        dna       <- DnaString.from(motif).left.map(_.toString)
        contents  <- toProbabilities(gcs)
        problem   <- ExpectedRestrictionSitesProblem.from(dna, n, contents).left.map(_.toString)
      } yield ExpectedRestrictionSites.expectedCounts(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses the three non-empty lines into `(n, motif, gcTokens)`. */
  private def parse(raw: String): Either[String, (Int, String, Vector[String])] = {
    val lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
    for {
      nLine <- lines.headOption.toRight("expected a length line")
      motif <- lines.lift(1).toRight("expected a motif line")
      aLine <- lines.lift(2).toRight("expected a GC-content line")
      n     <- nLine.toIntOption.toRight(s"invalid length '$nLine'")
      gcs = aLine.split("\\s+").iterator.filter(_.nonEmpty).toVector
    } yield (n, motif, gcs)
  }

  /** Parses and validates each GC-content token into a `Probability`. */
  private def toProbabilities(tokens: Vector[String]): Either[String, Vector[Probability]] =
    tokens.foldRight[Either[String, List[Probability]]](Right(Nil)) { (tok, acc) =>
      for {
        rest <- acc
        d    <- tok.toDoubleOption.toRight(s"invalid GC-content '$tok'")
        p    <- Probability.from(d).left.map(_.toString)
      } yield p :: rest
    }.map(_.toVector)
}
