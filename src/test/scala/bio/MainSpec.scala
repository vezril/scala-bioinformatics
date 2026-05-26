package bio

import cats.effect.ExitCode
import cats.effect.unsafe.implicits.global
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFunSpec with Matchers {

  describe("Main") {
    it("run returns ExitCode.Success") {
      val result = Main.run(List.empty).unsafeRunSync()
      result shouldBe ExitCode.Success
    }
  }
}
