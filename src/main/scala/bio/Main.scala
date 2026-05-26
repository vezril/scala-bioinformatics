package bio

import bio.problems.{InferringmRNAFromProteinProblem, MortalFibonacciProblem}
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val result = InferringmRNAFromProteinProblem.solve()

    result.as(ExitCode.Success)
  }
}
