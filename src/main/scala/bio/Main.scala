package bio

import bio.problems.MortalFibonacciProblem
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val result = MortalFibonacciProblem.solve()

    result.as(ExitCode.Success)
  }
}
