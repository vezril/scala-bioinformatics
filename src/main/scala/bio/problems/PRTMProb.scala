package bio.problems

import bio.algorithms.protein.ProteinMass
import bio.domain.protein.{ProteinMassProblem, ProteinString}
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind PRTM ("Calculating Protein Mass") runner.
  *
  * Reads a protein string from `prtm_data.txt`, validates it into a
  * [[ProteinMassProblem]], computes the total monoisotopic mass, and prints the
  * mass (to three decimals) through `IO`. Any validation error — invalid character
  * or over-long protein, or a missing protein — yields a printed error message
  * rather than a thrown exception.
  */
object PRTMProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/prtm_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      result = for {
        line <- raw
          .split("\\R")
          .iterator
          .map(_.trim)
          .find(_.nonEmpty)
          .toRight("no protein string found in prtm_data.txt")
        protein <- ProteinString.from(line).left.map(_.toString)
        problem <- ProteinMassProblem.from(protein).left.map(_.toString)
      } yield ProteinMass.calculate(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
