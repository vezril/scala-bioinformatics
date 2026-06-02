package bio.problems

import bio.algorithms.graph.TrieConstruction
import bio.domain.graph.PatternTrieProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind TRIE ("Introduction to Pattern Matching") runner.
  *
  * Reads one DNA pattern per non-empty line from `trie_data.txt`, validates them
  * into a [[PatternTrieProblem]], constructs the trie, and prints its adjacency list
  * (each edge as `parent child symbol`) through `IO`. Any parse/validation error
  * yields a printed message rather than a thrown exception.
  */
object TRIEProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/trie_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        patterns <- parsePatterns(raw)
        problem  <- PatternTrieProblem.from(patterns).left.map(_.toString)
      } yield TrieConstruction.construct(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses each non-empty line into a validated `DnaString`. */
  private def parsePatterns(raw: String): Either[String, Vector[DnaString]] =
    raw
      .split("\\R")
      .iterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .toVector
      .foldRight[Either[String, List[DnaString]]](Right(Nil)) { (line, acc) =>
        for {
          rest <- acc
          dna  <- DnaString.from(line).left.map(_.toString)
        } yield dna :: rest
      }
      .map(_.toVector)
}
