package bio.problems

import bio.algorithms.genetics.InferGenotype
import bio.domain.genetics.PedigreeProblem
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind MEND ("Inferring Genotype from a Pedigree") runner.
  *
  * Reads a single Newick-format pedigree tree from `mend_data.txt`, parses it via
  * [[NewickParser]], builds a [[PedigreeProblem]], infers the root individual's genotype
  * distribution, and prints the three probabilities (AA, Aa, aa) through `IO`. Any
  * parse/validation error yields a printed message rather than a thrown exception.
  */
object MENDProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mend_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        line    <- raw.split("\\R").iterator.map(_.trim).find(_.nonEmpty).toRight("empty input")
        tree    <- NewickParser.parse(line).left.map(e => s"parse error: $e")
        problem <- PedigreeProblem.from(tree).left.map(_.toString)
      } yield InferGenotype.infer(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
