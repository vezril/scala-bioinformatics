package bio.problems

import bio.algorithms.analysis.ShortestCommonSupersequence
import bio.domain.analysis.SupersequenceProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind SCSP ("Interleaving Two Motifs") runner.
  *
  * Reads two plain DNA strings `s` and `t` (the first two non-empty lines) from
  * `scsp_data.txt`, validates them into a [[SupersequenceProblem]], computes a shortest
  * common supersequence, and prints it through `IO`. Any parse/validation error — or
  * fewer than two lines — yields a printed message rather than a thrown exception.
  */
object SCSPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/scsp_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = {
        val lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
        for {
          pair <- lines match {
            case s +: t +: _ => Right((s, t))
            case _           => Left(s"expected at least two DNA lines, got ${lines.size}")
          }
          s       <- DnaString.from(pair._1).left.map(_.toString)
          t       <- DnaString.from(pair._2).left.map(_.toString)
          problem <- SupersequenceProblem.from(s, t).left.map(_.toString)
        } yield ShortestCommonSupersequence.build(problem)
      }
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
