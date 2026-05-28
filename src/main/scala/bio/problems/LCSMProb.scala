package bio.problems

import bio.algorithms.analysis.SharedMotif
import bio.domain.analysis.SharedMotifProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths
import cats.effect.unsafe.implicits.{global => ioRuntime}

object LCSMProb {

  def solve(): IO[Unit] = {

    val path = Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/shared_motif.txt")

    val r = for {
      data <- FastaFileReader.read(path).attempt.unsafeRunSync()
      records <- data
      dnaStrings = records
        .map(_.dna)
        .toVector
      problem <- SharedMotifProblem.from(dnaStrings)
      result = SharedMotif.find(problem)
    } yield result

    r match {
      case Left(err) => IO.println(err)
      case Right(v) => IO.println(v)
    }

  }

}
