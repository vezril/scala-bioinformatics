package bio

import bio.algorithms.analysis.{HammingDistance, MotifLocations}
import bio.algorithms.genetics.ExpectedOffspring
import bio.algorithms.protein.RnaTranslation
import bio.domain.genetics.CouplePopulation
import bio.domain.nucleic.{DnaString, RnaString}
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val input = "1 0 0 1 0 1"
    //
    val maybePop = CouplePopulation.from(17161, 17763 , 17757, 16761, 19376, 18438)

    maybePop match {
      case Left(_) => IO.println("Hello, Bioinformatics!").as(ExitCode.Success)
      case Right(pop) =>
        val out = ExpectedOffspring.dominantPhenotype(pop)
        IO.println(s"${out}").as(ExitCode.Success)
    }



    //IO.println("Hello, Bioinformatics!").as(ExitCode.Success)
  }
}
