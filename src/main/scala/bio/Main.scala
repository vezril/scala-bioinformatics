package bio

import bio.problems.{EnumGenesProblem, IndAllelesProblem, InferringmRNAFromProteinProblem, MortalFibonacciProblem, PartialPermProblem}
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    //val result = IndAllelesProblem.solve()
    //val result = EnumGenesProblem.solve()
    val result = PartialPermProblem.solve()

    result.as(ExitCode.Success)
  }
}
