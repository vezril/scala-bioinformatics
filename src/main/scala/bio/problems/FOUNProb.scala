package bio.problems

import bio.algorithms.genetics.WrightFisher
import bio.domain.genetics.WrightFisherFixationProblem
import cats.effect.IO

object FOUNProb {

  def solve(): IO[Unit] = {

    val n = 18
    val m =  4
    val inputString = "1 4 11"

    val v = inputString.split(" ")
      .toVector
      .map(_.toInt)

    val r = for {
      problem <- WrightFisherFixationProblem.from(n, m, v)
      result = WrightFisher.fixationLogProbs(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"${err.toString}")
      case Right(value) =>
        val out = value
          .foldLeft("")((acc1, v1) => acc1 ++ s"${v1.foldLeft("")((acc2, v2) => acc2 ++ s"${v2.toString} ")}\n")
        IO.println(out)
    }
  }

}
