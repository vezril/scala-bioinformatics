package bio.problems

import bio.algorithms.combinatorics.EnumerateKmers
import bio.domain.combinatorics.KmerEnumerationProblem
import cats.effect.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import scala.util.Try

object LEXFProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/lexf_data.txt"

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      lines = raw.split("\\R").iterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- lines match {
        case alphabetLine +: lengthLine +: _ =>
          val alphabet = alphabetLine.split("\\s+").iterator.filter(_.nonEmpty).map(_.head).toVector
          val result = for {
            length  <- Try(lengthLine.toInt).toEither.left.map(_ => s"invalid length: $lengthLine")
            problem <- KmerEnumerationProblem.from(alphabet, length).left.map(_.toString)
          } yield EnumerateKmers.enumerate(problem)
          result match {
            case Left(err)  => IO.println(err)
            case Right(res) => IO.println(res.format)
          }
        case _ =>
          IO.println("expected an alphabet line followed by a length line")
      }
    } yield ()
}
