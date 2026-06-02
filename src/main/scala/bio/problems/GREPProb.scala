package bio.problems

import bio.algorithms.graph.CompleteCycleAssembly
import bio.domain.graph.CompleteCycleProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind GREP ("Genome Assembly with Perfect Coverage and Repeats") runner.
  *
  * Reads newline-separated DNA (k+1)-mers from `grep_data.txt`, validates them into a
  * [[CompleteCycleProblem]], enumerates every complete cycle in their de Bruijn graph,
  * and prints the assembled circular strings (one per line) through `IO`. Invalid
  * input yields a printed error message rather than a thrown exception.
  */
object GREPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/grep_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      result = for {
        reads <- lines
          .foldRight[Either[String, List[DnaString]]](Right(Nil)) { (line, acc) =>
            for {
              rest <- acc
              dna  <- DnaString.from(line).left.map(err => s"invalid read '$line': $err")
            } yield dna :: rest
          }
          .map(_.toVector)
        problem <- CompleteCycleProblem.from(reads).left.map(_.toString)
      } yield CompleteCycleAssembly.assemble(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
