package bio

import bio.problems._
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    //val result = LIAProb.solve()
    //val result = PERMProb.solve()
    //val result = PPERProb.solve()
    //val result = GRPHProb.solve()
    //val result = CONSProb.solve()
    //val result = PROBProb.solve()
    //val result = TREEProb.solve()
    //val result = SPLCProb.solve()
    val result = SSETProb.solve()

    result.as(ExitCode.Success)
  }
}
