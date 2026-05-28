package bio.problems

import bio.algorithms.analysis.FailureArray
import bio.domain.analysis.FailureArrayProblem
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Paths
import cats.effect.unsafe.implicits.{global => ioRuntime}

object KMPProb {

  def solve(): IO[Unit] = {

    val path = Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/kmp_prob.txt")

    val r = for {
      parsed <- FastaFileReader.read(path).unsafeRunSync()
      problems = parsed.map { r =>
        FailureArrayProblem.from(r.dna)
      }.collect { case Right(prob) => prob }
      results =  problems.map(FailureArray.compute)
    } yield results

    r match {
      case Left(e) => IO.println(s"err: ${e}")
      case Right(results) =>
        val out = results
          .foldLeft("")((acc1, f) => acc1 ++ s"${f.foldLeft("")((acc2, v) => acc2 ++ s"${v.toString} ")}\n")
        IO.println(out)
    }

  }
}
