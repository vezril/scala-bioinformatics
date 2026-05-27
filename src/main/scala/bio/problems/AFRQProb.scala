package bio.problems

import bio.algorithms.genetics.DiseaseCarriers
import bio.domain.stats.Probability
import cats.effect.IO

object AFRQProb {

  def solve(): IO[Unit] = {
    val inputString = "0.763166575484 0.829635233674 0.230745990412 0.860590841189 0.500255633608 0.549022617957 0.601199313199 0.73088505234 0.312391428964 0.676070125812 0.305824722582 0.0278174908017 0.306301803597 0.552369659801 0.62206200977 0.824383968389 0.33419637149 0.443913001643 0.303703272397 0.508958044517"

    val a = inputString
      .split(" ")
      .toVector
      .map(_.toDouble)
      .map(Probability.unsafeFrom)


    def format(n: Double): String = {
      BigDecimal(n)
        .setScale(3, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
        .toString
    }

    val r =
      DiseaseCarriers
        .frequencies(a)
        .map(_.value)
        .map(format)
        .fold("")((acc, result) => acc + s"${result} ")

    IO.println(s"${r}")
  }

}
