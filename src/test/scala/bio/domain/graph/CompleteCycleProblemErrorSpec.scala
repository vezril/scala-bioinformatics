package bio.domain.graph

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CompleteCycleProblemErrorSpec extends AnyFunSpec with Matchers {

  describe("CompleteCycleProblemError") {
    it("is a sealed trait with the expected cases and accessors") {
      val empty: CompleteCycleProblemError =
        CompleteCycleProblemError.EmptyKmerCollection
      val tooMany: CompleteCycleProblemError =
        CompleteCycleProblemError.TooManyReads(51, 50)
      val tooShort: CompleteCycleProblemError =
        CompleteCycleProblemError.KmerTooShort(1, 1, 2)
      val tooLong: CompleteCycleProblemError =
        CompleteCycleProblemError.KmerTooLong(1, 7, 6)
      val inconsistent: CompleteCycleProblemError =
        CompleteCycleProblemError.InconsistentLength(1, 2, 3)

      empty shouldBe CompleteCycleProblemError.EmptyKmerCollection

      tooMany shouldBe CompleteCycleProblemError.TooManyReads(51, 50)
      CompleteCycleProblemError.TooManyReads(51, 50).count shouldBe 51
      CompleteCycleProblemError.TooManyReads(51, 50).max shouldBe 50

      CompleteCycleProblemError.KmerTooShort(1, 1, 2).index shouldBe 1
      CompleteCycleProblemError.KmerTooShort(1, 1, 2).length shouldBe 1
      CompleteCycleProblemError.KmerTooShort(1, 1, 2).min shouldBe 2

      CompleteCycleProblemError.KmerTooLong(1, 7, 6).index shouldBe 1
      CompleteCycleProblemError.KmerTooLong(1, 7, 6).length shouldBe 7
      CompleteCycleProblemError.KmerTooLong(1, 7, 6).max shouldBe 6

      CompleteCycleProblemError.InconsistentLength(1, 2, 3).index shouldBe 1
      CompleteCycleProblemError.InconsistentLength(1, 2, 3).length shouldBe 2
      CompleteCycleProblemError.InconsistentLength(1, 2, 3).expected shouldBe 3

      tooShort shouldBe a[CompleteCycleProblemError]
      tooLong shouldBe a[CompleteCycleProblemError]
      inconsistent shouldBe a[CompleteCycleProblemError]
    }
  }
}
