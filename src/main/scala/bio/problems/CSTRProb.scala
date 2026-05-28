package bio.problems

import bio.algorithms.analysis.GeneticCharacterTable
import bio.domain.analysis.GeneticCharacterTableProblem
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import cats.effect.unsafe.implicits.{global => ioRuntime}

object CSTRProb {

  def solve(): IO[Unit] = {

    val path = Paths.get("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/cstr_prob.txt")

    val r = for {
      raw <- IO
        .blocking(new String(Files.readAllBytes(path), StandardCharsets.UTF_8))
        .attempt
        .unsafeRunSync()
        .map(_.split("\n"))
      sequences = raw
        .toVector
        .map(DnaString.unsafeFrom)
      prob <- GeneticCharacterTableProblem.from(sequences)
      result = GeneticCharacterTable.compute(prob)
    } yield result

    r match {
      case Left(err) => IO.println(s"${err}")
      case Right(v) =>
        val out = v.fold("")((acc, vec) => acc + s"${vec}\n")

        IO.println(out)
    }
  }

}
