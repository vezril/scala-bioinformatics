package bio.problems

import bio.algorithms.protein.MatchSpectrum
import bio.domain.protein.{ProteinString, SpectrumMatchProblem}
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind PRSM ("Matching a Spectrum to a Protein") runner.
  *
  * Reads from `prsm_data.txt`: line 1 is the count `n`, the next `n` lines are the
  * candidate proteins, and the remaining non-empty lines are the target spectrum `R`.
  * Validates them into a [[SpectrumMatchProblem]], finds the candidate maximising the
  * multiplicity of `R⊖S[s_k]`, and prints the multiplicity and protein through `IO`.
  * Any parse/validation error yields a printed message rather than a thrown exception.
  */
object PRSMProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/prsm_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        parsed <- parse(raw)
        (proteins, spectrum) = parsed
        problem <- SpectrumMatchProblem.from(proteins, spectrum).left.map(_.toString)
      } yield MatchSpectrum.bestMatch(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses `n`, the `n` proteins, and the remaining lines as spectrum values. */
  private def parse(raw: String): Either[String, (Vector[ProteinString], Vector[Double])] = {
    val lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
    for {
      header <- lines.headOption.toRight("expected a count line")
      n      <- header.toIntOption.toRight(s"invalid count '$header'")
      rest = lines.drop(1)
      proteinLines = rest.take(n)
      spectrumLines = rest.drop(n)
      proteins <- proteinLines.foldRight[Either[String, List[ProteinString]]](Right(Nil)) {
        (line, acc) =>
          for {
            tail <- acc
            p    <- ProteinString.from(line).left.map(_.toString)
          } yield p :: tail
      }.map(_.toVector)
      spectrum <- spectrumLines.foldRight[Either[String, List[Double]]](Right(Nil)) {
        (line, acc) =>
          for {
            tail <- acc
            d    <- line.toDoubleOption.toRight(s"invalid mass '$line'")
          } yield d :: tail
      }.map(_.toVector)
    } yield (proteins, spectrum)
  }
}
