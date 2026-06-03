package bio.problems

import bio.algorithms.analysis.InterwovenMotifs
import bio.domain.analysis.InterwovenMotifProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind ITWV ("Finding Disjoint Motifs in a Gene") runner.
  *
  * Reads the text DNA string (first nonblank line) and the pattern strings
  * (remaining nonblank lines) from `itwv_data.txt`, computes the pairwise
  * interweaving matrix, and prints it through `IO` (rows space-separated, one
  * per line). Any parse/validation error yields a printed message rather than a
  * thrown exception.
  */
object ITWVProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/itwv_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      _ <- solveAll(raw) match {
        case Left(err)     => IO.println(err)
        case Right(matrix) => IO.println(matrix.format)
      }
    } yield ()

  private def solveAll(raw: String): Either[String, bio.domain.analysis.InterwovenMotifMatrix] = {
    val lines = raw.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
    lines match {
      case textLine +: patternLines =>
        for {
          text     <- DnaString.from(textLine).left.map(e => s"invalid text: $e")
          patterns <- patternLines.foldLeft[Either[String, Vector[DnaString]]](Right(Vector.empty)) {
            (acc, line) =>
              for {
                ps <- acc
                d  <- DnaString.from(line).left.map(e => s"invalid pattern '$line': $e")
              } yield ps :+ d
          }
          problem <- InterwovenMotifProblem.from(text, patterns).left.map(_.toString)
        } yield InterwovenMotifs.compute(problem)
      case _ => Left("expected at least a text line")
    }
  }
}
