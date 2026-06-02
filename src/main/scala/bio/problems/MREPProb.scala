package bio.problems

import bio.algorithms.graph.IdentifyMaximalRepeats
import bio.domain.graph.MaximalRepeatProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind MREP ("Identifying Maximal Repeats") runner.
  *
  * Reads the DNA string `s` from `mrep_data.txt` (a single non-empty line), validates
  * it into a [[MaximalRepeatProblem]] with the Rosalind minimum length of 20, finds all
  * maximal repeats of length ≥ 20, and prints them (one per line) through `IO`. Any
  * validation error yields a printed message rather than a thrown exception.
  */
object MREPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mrep_data.txt"

  private val MinLength: Int = 20

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        line    <- raw.split("\\R").iterator.map(_.trim).find(_.nonEmpty).toRight("empty input")
        dna     <- DnaString.from(line).left.map(_.toString)
        problem <- MaximalRepeatProblem.from(dna, MinLength).left.map(_.toString)
      } yield IdentifyMaximalRepeats.find(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
