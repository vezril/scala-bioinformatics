package bio.problems

import bio.algorithms.nucleic.NoncrossingMatching
import bio.domain.nucleic.{NoncrossingMatchingProblem, RnaString}
import cats.effect.IO

object CATProb {

  def solve(): IO[Unit] = {

    val rna = RnaString.unsafeFrom("AGGUAGGCCGCAUGAUCUACGCCGUACGUAUAUACAUGUAUAUUGCAGCAUUGCAUGCUAUAAUAAGCCGAUCGAGCUAUAAUAUAUACGGCUAUAUUGCAAUUCAAUGCUAUAUUCUUCUCAGCCUAGUAAUUCGGGGCGCCAUAUAAUGAUCGAUCGGCCGUUCGAGUUAACUAUAUAGCGGCCAAUUACGAGCAUGAUUGCAAUAUCGCCGAUGCUAAUCGGUGGCCGCACAUAAGCUACAUCAUGUAGUCAGCUGAUCCUACGUCGAUAGGCGUGCAUAUUAAGAUAUAUAUGC")

    val r = for {
      problem <- NoncrossingMatchingProblem.from(rna)
      result = NoncrossingMatching.count(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"${err}")
      case Right(value) => IO.println(value)
    }
  }

}
