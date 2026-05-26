package bio

import bio.problems.{IndAllelesProblem, InferringmRNAFromProteinProblem, MortalFibonacciProblem}
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val result = IndAllelesProblem.solve()

    result.as(ExitCode.Success)
  }
}
