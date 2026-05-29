package bio.problems

import bio.algorithms.protein.AffineGapAlignment
import bio.domain.protein.{AffineGapAlignmentProblem, ProteinString}
import cats.effect.IO

object GAFFProb {

  def solve(): IO[Unit] = {

    val s = ProteinString
      .from("TGFTRLQRNHFNAQSYIVNDYTWAKDLFADQPHGELVHLRNKFYRACVSDPFMENNIWTC\nCNMQQFHCTRRDPCH".replace("\n", ""))
      .getOrElse(sys.error("invalid left protein string"))
    val t = ProteinString
      .from("TGFTRLQRNHFNAYIVNDYPVAVQPHGELVHLRNKFYRACVSDPFMENNIWTCCCIKRVG\nFYFRRDPCH".replace("\n", ""))
      .getOrElse(sys.error("invalid right protein string"))

    val r = for {
      problem <- AffineGapAlignmentProblem.from(s, t)
      result = AffineGapAlignment.compute(problem)
    } yield result

    r match {
      case Left(err) => IO.println(s"$err")
      case Right(alignment) =>
        IO.println(alignment.score) *>
          IO.println(alignment.augmentedLeft) *>
          IO.println(alignment.augmentedRight)
    }
  }
}
