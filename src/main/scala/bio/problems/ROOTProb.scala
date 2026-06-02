package bio.problems

import bio.algorithms.combinatorics.RootedBinaryTrees
import bio.domain.combinatorics.RootedTreeLeafCount
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind ROOT ("Counting Rooted Binary Trees") runner.
  *
  * Reads `n` from `root_data.txt` (a single integer), validates it into a
  * [[RootedTreeLeafCount]], computes `B(n) = (2n−3)!! mod 1,000,000`, and prints the
  * result through `IO`. Any parse/validation error yields a printed message rather than
  * a thrown exception.
  */
object ROOTProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/root_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        token <- raw.split("\\s+").iterator.map(_.trim).find(_.nonEmpty).toRight("empty input")
        n     <- token.toIntOption.toRight(s"invalid integer '$token'")
        count <- RootedTreeLeafCount.from(n).left.map(_.toString)
      } yield RootedBinaryTrees.count(count)
      _ <- result match {
        case Left(err)    => IO.println(err)
        case Right(value) => IO.println(value.toString)
      }
    } yield ()
}
