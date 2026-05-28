package bio.problems

import bio.algorithms.nucleic.MotzkinMatching
import bio.domain.nucleic.{MotzkinMatchingProblem, RnaString}
import cats.effect.IO

object MOTZProb {

  def solve(): IO[Unit] = {

    val rna = RnaString.unsafeFrom("CUCGUAAAAUGUACAACACGCGCACGAUCUGUGUCGCUAUGGGCGGCAAUCGUGGCUUGUCUCAGUACGGACACACGCGUUGGUGAAUCUCGGAGUAUUAAGGUCCUCGGCCCUUAUUCACAGGUAGGCCUUUAUACGGCCUGAUUGCCAAACUUCGGCCUUCGUGCUGAGCAGGACGUACGCUCGGACAAGCCUGAAGUAGCCCCCUCCUUGGGUAUGACGGCGGCGGCGAGUUGGAUCUUGAUGCGGUUUCUUGACGAGCUGAGGUUAUAA")

    val r = for {
      problem <- MotzkinMatchingProblem.from(rna)
      result = MotzkinMatching.count(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"$err")
      case Right(value) => IO.println(value)
    }
  }

}
