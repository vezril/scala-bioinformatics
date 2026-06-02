package bio.problems

import bio.algorithms.graph.EnumerateUnrootedBinaryTrees
import bio.domain.graph.UnrootedBinaryTreesProblem
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind EUBT ("Enumerating Unrooted Binary Trees") runner.
  *
  * Reads the taxa from `eubt_data.txt` (a single whitespace-separated line), validates
  * them into an [[UnrootedBinaryTreesProblem]], enumerates every unrooted binary tree,
  * and prints each in Newick format (one per line) through `IO`. Any validation error
  * yields a printed message rather than a thrown exception.
  */
object EUBTProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/eubt_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      taxa = raw.split("\\s+").iterator.filter(_.nonEmpty).toVector
      result = UnrootedBinaryTreesProblem.from(taxa).left.map(_.toString)
      _ <- result match {
        case Left(err)      => IO.println(err)
        case Right(problem) => IO.println(EnumerateUnrootedBinaryTrees.enumerate(problem).format)
      }
    } yield ()
}
