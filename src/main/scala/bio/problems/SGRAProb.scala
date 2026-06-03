package bio.problems

import bio.algorithms.protein.SpectrumGraph
import bio.domain.protein.SpectrumGraphProblem
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind SGRA ("Using the Spectrum Graph to Infer Peptides") runner.
  *
  * Reads the mass list `L` from `sgra_data.txt` (one positive real per non-empty line),
  * validates it into a [[SpectrumGraphProblem]], infers the longest protein matching the
  * spectrum graph, and prints it through `IO`. Any parse/validation error yields a
  * printed message rather than a thrown exception.
  */
object SGRAProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/sgra_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        masses  <- parseMasses(raw)
        problem <- SpectrumGraphProblem.from(masses).left.map(_.toString)
      } yield SpectrumGraph.longestPeptide(problem)
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
