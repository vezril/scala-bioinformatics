package bio.problems

import bio.algorithms.protein.InferProteinFromSpectrum
import bio.domain.protein.PrefixSpectrum
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind SPEC ("Inferring Protein from Spectrum") runner.
  *
  * Reads newline-separated prefix weights from `spec_data.txt`, validates them into
  * a [[PrefixSpectrum]], reconstructs the protein by mapping each consecutive
  * weight difference to the nearest monoisotopic amino-acid mass, and prints the
  * protein through `IO`. Any parse/validation error yields a printed error message
  * rather than a thrown exception.
  */
object SPECProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/spec_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      result = for {
        weights <- lines
          .foldRight[Either[String, List[Double]]](Right(Nil)) { (line, acc) =>
            for {
              rest <- acc
              w <- line.toDoubleOption
                .toRight(s"invalid weight '$line': not a number")
            } yield w :: rest
          }
          .map(_.toVector)
        problem <- PrefixSpectrum.from(weights).left.map(_.toString)
      } yield InferProteinFromSpectrum.infer(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
