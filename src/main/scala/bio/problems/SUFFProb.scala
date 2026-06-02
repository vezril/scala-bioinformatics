package bio.problems

import bio.algorithms.graph.SuffixTreeConstruction
import bio.domain.graph.SuffixTreeProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind SUFF ("Encoding Suffix Trees") runner.
  *
  * Reads the DNA string `s` from `suff_data.txt` (a single line, optionally already
  * carrying a trailing `$`, which is stripped), validates it into a
  * [[SuffixTreeProblem]], constructs the suffix tree of `s$`, and prints the substrings
  * labelling its edges (one per line) through `IO`. Any validation error yields a
  * printed message rather than a thrown exception.
  */
object SUFFProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/suff_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        line <- raw.split("\\R").iterator.map(_.trim).find(_.nonEmpty).toRight("empty input")
        s = line.stripSuffix("$")
        dna     <- DnaString.from(s).left.map(_.toString)
        problem <- SuffixTreeProblem.from(dna).left.map(_.toString)
      } yield SuffixTreeConstruction.encode(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
