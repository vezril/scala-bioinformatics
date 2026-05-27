package bio.parsing

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class FastaErrorSpec extends AnyFunSpec with Matchers {

  describe("FastaError.IoFailure") {
    it("carries the underlying throwable") {
      val cause = new java.nio.file.NoSuchFileException("missing.fa")
      val err   = FastaError.IoFailure(cause)
      err.cause shouldBe a[java.nio.file.NoSuchFileException]
      err.cause.getMessage shouldBe "missing.fa"
    }
  }

  describe("FastaError.Parse") {
    it("wraps a FastaParseError") {
      val err = FastaError.Parse(FastaParseError.MissingHeader)
      err.error shouldBe FastaParseError.MissingHeader
    }
  }
}
