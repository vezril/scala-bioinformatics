package bio.problems

import bio.algorithms.graph.NewickDistance
import bio.domain.graph.NewickDistanceProblem
import bio.parsing.NewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

/** Rosalind NWCK — "Distances in Trees".
  *
  * Input file format (per Rosalind): a sequence of blocks separated by one or
  * more blank lines. Each block has:
  *   - line 1: a Newick-format tree terminated by `;`
  *   - line 2: two labels `x y` separated by whitespace
  *
  * Output: the per-block distances, separated by single spaces, on one line.
  */
object NWCKProb {

  private val InputPath: Path = Paths.get(
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/newick_trees.txt"
  )

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(InputPath), StandardCharsets.UTF_8))
      _ <- solveAll(raw) match {
        case Left(err)       => IO.println(s"NWCK error: $err")
        case Right(distances) => IO.println(distances.mkString(" "))
      }
    } yield ()

  /** Parses the multi-block input and returns the per-block distances, or the
    * first error encountered.
    */
  private[problems] def solveAll(raw: String): Either[String, Vector[Int]] =
    splitBlocks(raw).foldLeft[Either[String, Vector[Int]]](Right(Vector.empty)) {
      (acc, block) =>
        for {
          dists <- acc
          d     <- solveBlock(block)
        } yield dists :+ d
    }

  /** Splits the raw input into blocks. A block is one or more contiguous
    * non-blank lines; blocks are separated by one or more blank lines.
    */
  private def splitBlocks(raw: String): Vector[Vector[String]] = {
    val (final0, current0) = raw.linesIterator
      .map(_.trim)
      .foldLeft((Vector.empty[Vector[String]], Vector.empty[String])) {
        case ((completed, current), line) =>
          if (line.isEmpty)
            if (current.isEmpty) (completed, current)
            else (completed :+ current, Vector.empty[String])
          else (completed, current :+ line)
      }
    if (current0.nonEmpty) final0 :+ current0 else final0
  }

  /** Solves one block: line 0 is the Newick tree, line 1 is `x y`. */
  private def solveBlock(lines: Vector[String]): Either[String, Int] =
    lines match {
      case Vector(treeLine, queryLine) =>
        val parts = queryLine.split("\\s+").filter(_.nonEmpty)
        if (parts.length != 2)
          Left(s"expected two whitespace-separated labels, got: $queryLine")
        else
          for {
            tree    <- NewickParser.parse(treeLine).left.map(e => s"parse error: $e")
            problem <- NewickDistanceProblem
              .from(tree, parts(0), parts(1))
              .left
              .map(e => s"problem-construction error: $e")
          } yield NewickDistance.between(problem)

      case other =>
        Left(s"expected a 2-line block (newick + query), got ${other.size} line(s): $other")
    }
}
