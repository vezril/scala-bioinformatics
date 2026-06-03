package bio.problems

import bio.algorithms.graph.WeightedNewickDistance
import bio.domain.graph.WeightedTreeDistanceProblem
import bio.parsing.WeightedNewickParser
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Rosalind NKEW ("Newick Format with Edge Weights") runner.
  *
  * Input is a sequence of blocks separated by blank lines; each block has a weighted
  * Newick tree (line 1) and a query pair `x y` (line 2). Prints the per-block distances,
  * each rendered without a trailing `.0` when whole, space-separated on one line. Any
  * parse/validation error yields a printed message rather than a thrown exception.
  */
object NKEWProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/nkew_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8))
      result = solveAll(raw)
      _ <- result match {
        case Left(err)        => IO.println(err)
        case Right(distances) => IO.println(distances.map(render).mkString(" "))
      }
    } yield ()

  private def solveAll(raw: String): Either[String, Vector[Double]] =
    splitBlocks(raw).foldLeft[Either[String, Vector[Double]]](Right(Vector.empty)) { (acc, block) =>
      for {
        dists <- acc
        d     <- solveBlock(block)
      } yield dists :+ d
    }

  private def splitBlocks(raw: String): Vector[Vector[String]] = {
    val (completed, current) = raw.linesIterator
      .map(_.trim)
      .foldLeft((Vector.empty[Vector[String]], Vector.empty[String])) {
        case ((done, cur), line) =>
          if (line.isEmpty) if (cur.isEmpty) (done, cur) else (done :+ cur, Vector.empty[String])
          else (done, cur :+ line)
      }
    if (current.nonEmpty) completed :+ current else completed
  }

  private def solveBlock(lines: Vector[String]): Either[String, Double] =
    lines match {
      case Vector(treeLine, queryLine) =>
        val parts = queryLine.split("\\s+").filter(_.nonEmpty)
        if (parts.length != 2) Left(s"expected two labels, got: $queryLine")
        else
          for {
            tree    <- WeightedNewickParser.parse(treeLine).left.map(e => s"parse error: $e")
            problem <- WeightedTreeDistanceProblem.from(tree, parts(0), parts(1)).left.map(_.toString)
          } yield WeightedNewickDistance.between(problem)
      case other => Left(s"expected a 2-line block, got ${other.size}: $other")
    }

  /** Render a distance, dropping a trailing `.0` for whole numbers. */
  private def render(d: Double): String =
    if (d.isWhole) d.toLong.toString else d.toString
}
