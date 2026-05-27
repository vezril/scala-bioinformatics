package bio.problems

import bio.algorithms.analysis.OverlapGraph
import bio.domain.analysis.OverlapLength
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Path
import scala.concurrent.ExecutionContext
import cats.effect.unsafe.implicits.{global => ioRuntime}

object OverlapGraphsProblem {

  def solve(): IO[Unit] = {

    val path = Path.of("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/overlap_graph.txt")
    val fastaFile = FastaFileReader.read(path).unsafeRunSync()

    fastaFile match {
      case Left(_) => IO.println("")
      case Right(records) =>
        OverlapLength.from(3) match {
          case Left(_) => IO.println("")
          case Right(k) =>
            val out = OverlapGraph
              .adjacency(records = records.toVector, k = k)
              .map(edge => s"${edge.from} ${edge.to}\n")
              .fold("")((acc, edge) => acc ++ edge)
            IO.println(out)
        }

    }

  }

}
