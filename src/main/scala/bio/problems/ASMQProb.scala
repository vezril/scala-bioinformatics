package bio.problems

import bio.algorithms.assembly.AssemblyStatistics
import bio.domain.assembly.ContigCollection
import bio.domain.nucleic.DnaString
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ASMQProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/asmq_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      result = for {
        contigs <- lines
          .foldRight[Either[String, List[DnaString]]](Right(Nil)) { (line, acc) =>
            for {
              rest <- acc
              dna  <- DnaString.from(line).left.map(err => s"invalid contig '$line': $err")
            } yield dna :: rest
          }
          .map(_.toVector)
        collection <- ContigCollection.from(contigs).left.map(_.toString)
      } yield AssemblyStatistics.assess(collection)
      _ <- result match {
        case Left(err)  => IO.println(err)
        case Right(res) => IO.println(res.format)
      }
    } yield ()
}
