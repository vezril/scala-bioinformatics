package bio.parsing

import bio.domain.graph.UndirectedEdge
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

class TreeAdjacencyFileReaderSpec extends AnyFunSpec with Matchers {

  /** Writes `contents` to a fresh temp file, runs `body`, deletes the file. */
  private def withTempFile[A](contents: String)(body: Path => IO[A]): A = {
    val acquire: IO[Path] = IO.blocking {
      val p = Files.createTempFile("tree-adjacency-file-reader-spec-", ".txt")
      Files.write(p, contents.getBytes(StandardCharsets.UTF_8))
      p
    }
    val release: Path => IO[Unit] = p => IO.blocking { Files.deleteIfExists(p); () }
    acquire.bracket(body)(release).unsafeRunSync()
  }

  private def edge(u: Int, v: Int): UndirectedEdge =
    UndirectedEdge.from(u, v).getOrElse(sys.error(s"invalid edge fixture ($u, $v)"))

  describe("TreeAdjacencyFileReader.parse (pure)") {
    it("parses the canonical Rosalind sample") {
      val content =
        """10
          |1 2
          |2 8
          |4 10
          |5 9
          |6 10
          |7 9
          |""".stripMargin
      TreeAdjacencyFileReader.parse(content) shouldBe Right(
        (
          10,
          Vector(edge(1, 2), edge(2, 8), edge(4, 10), edge(5, 9), edge(6, 10), edge(7, 9))
        )
      )
    }

    it("parses an n-only file (no edges) as (n, empty)") {
      TreeAdjacencyFileReader.parse("5\n") shouldBe Right((5, Vector.empty))
    }

    it("tolerates blank lines and extra whitespace between edges") {
      val content =
        """  10
          |
          |1 2
          |
          |   2  8
          |""".stripMargin
      TreeAdjacencyFileReader.parse(content) shouldBe Right(
        (10, Vector(edge(1, 2), edge(2, 8)))
      )
    }

    it("rejects empty input as EmptyInput") {
      TreeAdjacencyFileReader.parse("") shouldBe Left(TreeAdjacencyParseError.EmptyInput)
    }

    it("rejects whitespace-only input as EmptyInput") {
      TreeAdjacencyFileReader.parse("   \n\n  \n") shouldBe
        Left(TreeAdjacencyParseError.EmptyInput)
    }

    it("rejects a non-integer first line as InvalidN") {
      TreeAdjacencyFileReader.parse("hello\n1 2\n") shouldBe
        Left(TreeAdjacencyParseError.InvalidN("hello"))
    }

    it("rejects an edge line with non-integer endpoints as InvalidEdgeLine") {
      val content = "10\n1 two\n"
      TreeAdjacencyFileReader.parse(content) shouldBe Left(
        TreeAdjacencyParseError.InvalidEdgeLine(
          lineNumber = 2,
          line = "1 two",
          reason = "non-integer endpoint"
        )
      )
    }

    it("rejects an edge line with wrong arity as InvalidEdgeLine") {
      val content = "10\n1 2 3\n"
      TreeAdjacencyFileReader.parse(content) shouldBe Left(
        TreeAdjacencyParseError.InvalidEdgeLine(
          lineNumber = 2,
          line = "1 2 3",
          reason = "expected two whitespace-separated integers"
        )
      )
    }

    it("rejects an edge that would be a self-loop as InvalidEdgeLine") {
      val content = "10\n7 7\n"
      TreeAdjacencyFileReader.parse(content) match {
        case Left(TreeAdjacencyParseError.InvalidEdgeLine(2, "7 7", reason)) =>
          reason should include("SelfLoop")
        case other =>
          fail(s"expected InvalidEdgeLine with SelfLoop reason, got $other")
      }
    }

    it("reports the first failing edge in input order with its line number") {
      val content =
        """5
          |1 2
          |bad line
          |3 4
          |""".stripMargin
      // "bad line" splits into two tokens — neither parses as Int → non-integer endpoint
      TreeAdjacencyFileReader.parse(content) shouldBe Left(
        TreeAdjacencyParseError.InvalidEdgeLine(
          lineNumber = 3,
          line = "bad line",
          reason = "non-integer endpoint"
        )
      )
    }
  }

  describe("TreeAdjacencyFileReader.read (IO)") {
    it("reads the Rosalind sample from disk") {
      val content =
        """10
          |1 2
          |2 8
          |4 10
          |5 9
          |6 10
          |7 9
          |""".stripMargin
      withTempFile(content) { path =>
        TreeAdjacencyFileReader.read(path).map {
          case Right((n, edges)) =>
            n shouldBe 10
            edges should have size 6
          case other => fail(s"expected Right((10, 6 edges)), got $other")
        }
      }
    }

    it("returns IoFailure when the file does not exist") {
      val missing = Paths.get("/tmp/tree-adjacency-file-reader-spec-does-not-exist-xyz123.txt")
      TreeAdjacencyFileReader.read(missing).unsafeRunSync() match {
        case Left(TreeAdjacencyError.IoFailure(cause)) =>
          cause shouldBe a[java.io.IOException]
        case other =>
          fail(s"expected Left(IoFailure(IOException)), got $other")
      }
    }

    it("returns Parse when the file content is malformed") {
      withTempFile("hello\n1 2\n") { path =>
        TreeAdjacencyFileReader.read(path).map { result =>
          result shouldBe Left(
            TreeAdjacencyError.Parse(TreeAdjacencyParseError.InvalidN("hello"))
          )
        }
      }
    }

    it("is referentially transparent — constructing the IO does not throw") {
      val missing = Paths.get("/tmp/tree-adjacency-file-reader-spec-not-evaluated-zzz999.txt")
      val program = TreeAdjacencyFileReader.read(missing)
      program shouldBe a[IO[_]]
    }
  }
}
