package bio.parsing

import bio.domain.nucleic.{DnaString, SequenceError}
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class FastaFileReaderSpec extends AnyFunSpec with Matchers {

  /** Writes `contents` to a fresh temp file, yields the path to `body`, and deletes
    * the file afterwards. All inside `IO` so the test runs through `unsafeRunSync`.
    */
  private def withTempFile[A](contents: String)(body: Path => IO[A]): A = {
    val acquire: IO[Path] = IO.blocking {
      val p = Files.createTempFile("fasta-file-reader-spec-", ".fa")
      Files.write(p, contents.getBytes(StandardCharsets.UTF_8))
      p
    }
    val release: Path => IO[Unit] = p => IO.blocking { Files.deleteIfExists(p); () }
    acquire.bracket(body)(release).unsafeRunSync()
  }

  private def dna(s: String): DnaString =
    DnaString.from(s).getOrElse(sys.error(s"invalid DnaString in fixture: $s"))

  describe("FastaFileReader.read") {
    it("returns the parsed records for a well-formed single-record file") {
      withTempFile(">Rosalind_0001\nACGTACGT") { path =>
        FastaFileReader.read(path).map { result =>
          result shouldBe Right(List(FastaRecord("Rosalind_0001", dna("ACGTACGT"))))
        }
      }
    }

    it("parses the canonical Rosalind sample into three records in order") {
      val sample =
        """>Rosalind_6404
          |CCTGCGGAAGATCGGCACTAGAATAGCCAGAACCGTTTCTCTGAGGCTTCCGGCCTTCCCTCCCACTAATAATTCTGAGG
          |>Rosalind_5959
          |CCATCGGTAGCGCATCCTTAGTCCAATTAAGTCCCTATCCAGGCGCTCCGCCGAAGGTCTATATCCATTTGTCAGCAGACACGC
          |>Rosalind_0808
          |CCACCCTCGTGGTATGGCTAGGCATTCAGGAACCGGAGAACGCTTCAGACCAGCCCGGACTGGGAACCTGCGGGCAGTAGGTGGAAT
          |""".stripMargin
      withTempFile(sample) { path =>
        FastaFileReader.read(path).map {
          case Right(records) =>
            records.map(_.id) shouldBe List("Rosalind_6404", "Rosalind_5959", "Rosalind_0808")
          case other =>
            fail(s"expected Right(3 records), got $other")
        }
      }
    }

    it("returns Right(List.empty) for an empty file") {
      withTempFile("") { path =>
        FastaFileReader.read(path).map { result =>
          result shouldBe Right(List.empty)
        }
      }
    }

    it("returns IoFailure(NoSuchFileException) when the file does not exist") {
      val missing = Paths.get("/tmp/fasta-file-reader-spec-does-not-exist-xyz123.fa")
      val result  = FastaFileReader.read(missing).unsafeRunSync()
      result match {
        case Left(FastaError.IoFailure(cause: java.nio.file.NoSuchFileException)) =>
          cause.getMessage should include("xyz123")
        case Left(FastaError.IoFailure(cause: java.io.IOException)) =>
          // Any IOException is acceptable per the spec — touch `cause` to keep the binding live.
          cause shouldBe a[java.io.IOException]
        case other =>
          fail(s"expected Left(IoFailure(IOException)), got $other")
      }
    }

    it("returns Parse(MissingHeader) when content has sequence before any header") {
      withTempFile("ACGT\n>Rosalind_0001\nACGT") { path =>
        FastaFileReader.read(path).map { result =>
          result shouldBe Left(FastaError.Parse(FastaParseError.MissingHeader))
        }
      }
    }

    it("returns Parse(InvalidDna) when content contains an invalid DNA character") {
      withTempFile(">Rosalind_0001\nACGTX") { path =>
        FastaFileReader.read(path).map { result =>
          result shouldBe Left(
            FastaError.Parse(
              FastaParseError.InvalidDna("Rosalind_0001", SequenceError.InvalidCharacter('X'))
            )
          )
        }
      }
    }

    it("is referentially transparent — describing the read does not perform it") {
      val missing = Paths.get("/tmp/fasta-file-reader-spec-not-evaluated-zzz999.fa")
      // Constructing the IO must not throw (no eager file access).
      val program = FastaFileReader.read(missing)
      program shouldBe a[IO[_]]
    }
  }
}
