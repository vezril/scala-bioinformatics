package bio.problems

import bio.algorithms.analysis.MatchingRandomMotifs
import bio.domain.analysis.RandomMotifProblem
import bio.domain.nucleic.DnaString
import bio.domain.stats.Probability
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind RSTR ("Matching Random Motifs") runner.
  *
  * Reads two lines from `rstr_data.txt` — the first holding the trial count `N` and
  * the GC-content `x` (whitespace-separated), the second holding the motif — then
  * prints the probability that at least one of `N` random strings equals the motif.
  * Any parse/validation error yields a printed message rather than a thrown
  * exception.
  */
object RSTRProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rstr_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = parse(raw).flatMap { case (trials, gc, motif) =>
        for {
          dna        <- DnaString.from(motif).left.map(_.toString)
          gcContent  <- Probability.from(gc).left.map(_.toString)
          problem    <- RandomMotifProblem.from(dna, trials, gcContent).left.map(_.toString)
        } yield MatchingRandomMotifs.probability(problem)
      }
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses the two non-empty lines into `(trials, gcContent, motif)`. */
  private def parse(raw: String): Either[String, (Int, Double, String)] = {
    val lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
    for {
      header <- lines.headOption.toRight("expected a header line with N and x")
      motif  <- lines.lift(1).toRight("expected a motif line")
      tokens = header.split("\\s+").filter(_.nonEmpty)
      n      <- tokens.lift(0).flatMap(_.toIntOption).toRight(s"invalid trial count in '$header'")
      x      <- tokens.lift(1).flatMap(_.toDoubleOption).toRight(s"invalid GC-content in '$header'")
    } yield (n, x, motif)
  }
}
