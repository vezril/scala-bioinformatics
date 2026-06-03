package bio.problems

import bio.algorithms.protein.InferPeptide
import bio.domain.protein.FullSpectrumProblem
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind FULL ("Inferring Peptide from Full Spectrum") runner.
  *
  * Reads the mass list `L` from `full_data.txt` (one positive real per non-empty line:
  * the parent mass followed by the b-ion/y-ion masses), validates it into a
  * [[FullSpectrumProblem]], reconstructs the peptide, and prints it through `IO`. Any
  * parse/validation error yields a printed message rather than a thrown exception.
  */
object FULLProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/full_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        masses  <- parseMasses(raw)
        problem <- FullSpectrumProblem.from(masses).left.map(_.toString)
      } yield InferPeptide.infer(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses each non-empty line into a `Double`. */
  private def parseMasses(raw: String): Either[String, Vector[Double]] =
    raw
      .split("\\R")
      .iterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .toVector
      .foldRight[Either[String, List[Double]]](Right(Nil)) { (line, acc) =>
        for {
          rest <- acc
          d    <- line.toDoubleOption.toRight(s"invalid mass '$line'")
        } yield d :: rest
      }
      .map(_.toVector)
}
