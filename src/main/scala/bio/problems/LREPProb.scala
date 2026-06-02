package bio.problems

import bio.algorithms.graph.LongestMultipleRepeat
import bio.domain.graph.{LongestRepeatProblem, SuffixTreeEdge}
import cats.effect.IO

import java.nio.file.{Files, Paths}

/** Rosalind LREP ("Finding the Longest Multiple Repeat") runner.
  *
  * Reads from `lrep_data.txt`: line 1 is the text `s$`, line 2 is the repeat threshold
  * `k`, and the remaining lines are suffix-tree edges (`parent child start length`).
  * Validates them into a [[LongestRepeatProblem]], finds the longest substring
  * occurring at least `k` times, and prints it through `IO`. Any parse/validation error
  * yields a printed message rather than a thrown exception.
  */
object LREPProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lrep_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath))))
      result = for {
        parsed <- parse(raw)
        (text, k, edges) = parsed
        problem <- LongestRepeatProblem.from(text, k, edges).left.map(_.toString)
      } yield LongestMultipleRepeat.find(problem)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()

  /** Parses the text, `k`, and the suffix-tree edge list. */
  private def parse(raw: String): Either[String, (String, Int, Vector[SuffixTreeEdge])] = {
    val lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
    for {
      text  <- lines.headOption.toRight("expected a text line")
      kLine <- lines.lift(1).toRight("expected a k line")
      k     <- kLine.toIntOption.toRight(s"invalid k '$kLine'")
      edges <- parseEdges(lines.drop(2))
    } yield (text, k, edges)
  }

  /** Parses each `parent child start length` line into a `SuffixTreeEdge`. */
  private def parseEdges(lines: Vector[String]): Either[String, Vector[SuffixTreeEdge]] =
    lines.foldRight[Either[String, List[SuffixTreeEdge]]](Right(Nil)) { (line, acc) =>
      for {
        rest <- acc
        edge <- line.split("\\s+").filter(_.nonEmpty) match {
          case Array(parent, child, start, length) =>
            for {
              s <- start.toIntOption.toRight(s"invalid start in '$line'")
              l <- length.toIntOption.toRight(s"invalid length in '$line'")
            } yield SuffixTreeEdge(parent, child, s, l)
          case _ => Left(s"malformed edge line: '$line'")
        }
      } yield edge :: rest
    }.map(_.toVector)
}
