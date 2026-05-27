package bio.problems

import bio.algorithms.genetics.WrightFisher
import bio.domain.genetics.WrightFisherExpectationProblem
import bio.domain.stats.Probability
import bio.utils.Utils
import cats.effect.IO

object EBINProb {

  def solve(): IO[Unit] = {

    val n = 809125
    val probString = "0 0.0856904364514 0.0967382739137 0.175673759429 0.198309923918 0.201784358953 0.221039026219 0.326793490823 0.336624152782 0.425486009172 0.438862315139 0.478670638154 0.531591501078 0.559010056375 0.606214412075 0.638287210389 0.910335031977 0.910568062105 1"

    val probabilities = probString
      .split(" ")
      .map(_.toDouble)
      .toVector
      .map(Probability.unsafeFrom)

    val r = for {
      problem <- WrightFisherExpectationProblem.from(n, probabilities)
      result = WrightFisher.expectedFrequencies(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"err: ${err}")
      case Right(v) =>
        val out = v
          .foldLeft("")((acc, p) => acc + s"${Utils.format(p)} ")
        IO.println(s"${out}")
    }
  }

}
