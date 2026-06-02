package bio.problems

import bio.algorithms.protein.SpectralConvolution
import bio.domain.protein.{MassMultiset, SpectralConvolutionProblem}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind CONV ("Comparing Spectra with the Spectral Convolution") runner.
  *
  * Reads two whitespace-separated lines of masses from `conv_data.txt`, validates
  * each into a [[MassMultiset]], computes the spectral convolution, and prints the
  * largest multiplicity followed by the absolute value of the maximizing shift
  * through `IO`. Any parse/validation error — a missing line, a non-numeric token,
  * or an invalid multiset — yields a printed error message rather than a thrown
  * exception.
  */
object CONVProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/conv_data.txt"

  private def parseMultiset(
      label: String,
      line: String
  ): Either[String, MassMultiset] =
    line
      .split("\\s+")
      .iterator
      .filter(_.nonEmpty)
      .toVector
      .foldRight[Either[String, List[Double]]](Right(Nil)) { (tok, acc) =>
        for {
          rest <- acc
          m <- tok.toDoubleOption.toRight(s"invalid mass '$tok' in $label")
        } yield m :: rest
      }
      .map(_.toVector)
      .flatMap(ms => MassMultiset.from(ms).left.map(e => s"$label: ${e.toString}"))

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      result = for {
        _ <- Either.cond(
          lines.length >= 2,
          (),
          "conv_data.txt must contain two mass lines"
        )
        s1 <- parseMultiset("S1", lines(0))
        s2 <- parseMultiset("S2", lines(1))
      } yield SpectralConvolution.convolve(SpectralConvolutionProblem(s1, s2))
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
