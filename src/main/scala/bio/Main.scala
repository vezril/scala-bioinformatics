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
    //val result = SSETProb.solve()
    //val result = ASPCProb.solve()
    //val result = INDCProb.solve()
    //val result = AFRQProb.solve()
    //val result = WFMDProb.solve()
    //val result = EBINProb.solve()
    //val result = FOUNProb.solve()
    //val result = INODProb.solve()
    //val result = NWCKProb.solve()
    //val result = KMPProb.solve()
    //val result = CTBLProb.solve()
    //val result = CSTRProb.solve()
    //val result = PMCHProb.solve()
    //val result = CATProb.solve()
    //val result = MOTZProb.solve()
    //val result = SSEQProb.solve()
    //val result = LCSMProb.solve()
    val result = EDITProb.solve()

    result.as(ExitCode.Success)
  }
}
