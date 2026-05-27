package bio.problems

import bio.domain.graph.TreeCompletionProblem
import cats.effect.IO

import bio.algorithms.graph.TreeCompletion
import bio.domain.graph.TreeCompletionProblem
import bio.parsing.TreeAdjacencyFileReader
import java.nio.file.Paths


object TREEProb {

  def solve(): IO[Unit] = {

    for {
      parsed <- TreeAdjacencyFileReader.read(Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/rosalind_tree.txt"))
      result = parsed.flatMap { case (n, edges) =>
        TreeCompletionProblem.from(n, edges)
          .map(TreeCompletion.edgesToAdd)
          .left.map(_.toString)
      }
      _ <- result match {
        case Left(err)  => IO.println(s"$err")
        case Right(out) => IO.println(s"$out")
      }
    } yield ()
  }
}
