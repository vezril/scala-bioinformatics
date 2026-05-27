package bio.problems

import bio.algorithms.genetics.WrightFisher
import bio.domain.genetics.WrightFisherProblem
import bio.utils.Utils
import cats.effect.IO

object WFMDProb {

  def solve(): IO[Unit] = {

    val n = 7
    val m = 9
    val g = 6
    val k = 5



    val maybeP = for {
      prob <- WrightFisherProblem.from(n, m, g, k)
      p = WrightFisher.atLeast(prob)
    } yield p

    maybeP match {
      case Left(err) => IO.println(s"err: ${err}")
      case Right(p) => IO.println(s"${Utils.format(p.value)}")
    }
  }

}
