package bio.problems

import bio.algorithms.matrix.ConsensusProfile
import bio.domain.matrix.ConsensusProfileProblem
import bio.domain.nucleic.DnaNucleotide.{A, C, G, T}
import bio.parsing.FastaFileReader
import cats.effect.IO

import java.nio.file.Path
import cats.effect.unsafe.implicits.{global => ioRuntime}

object CONSProb {

  def solve(): IO[Unit] = {

    val path = Path.of("/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/concensus_profile.txt")
    val maybeFastaRecods = FastaFileReader.read(path).unsafeRunSync()

    val out = for {
      maybeFastaRecords <- FastaFileReader.read(path).unsafeRunSync()
      records <- maybeFastaRecods.map(_.toVector)
      input <- ConsensusProfileProblem.from(records)
      result = ConsensusProfile.compute(input)
    } yield result

    def format(nuc: Option[Vector[Int]]): String = {
      nuc match {
        case None => ""
        case Some(vec) =>
          vec.foldLeft("")((acc, value) => acc ++ s"${value.toString} ")
      }
    }

    out match {
      case Left(_) => IO.println("")
      case Right(r) =>
        val out =
          s"${r.consensus.value}\n" +
          s"A: ${format(r.profile.counts.get(A))}\n" +
          s"C: ${format(r.profile.counts.get(C))}\n" +
          s"G: ${format(r.profile.counts.get(G))}\n" +
          s"T: ${format(r.profile.counts.get(T))}\n"

        IO.println(out)

    }


  }
}
