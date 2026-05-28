package bio.domain.nucleic

/** Validated input bundle for the Rosalind PMCH ("Perfect Matchings and RNA
  * Secondary Structures") problem — see
  * [[bio.algorithms.nucleic.PerfectMatching.count]].
  *
  * Wraps a balanced [[RnaString]] together with its precomputed pair counts:
  *   - `auCount` = `#A == #U` (the common value),
  *   - `cgCount` = `#C == #G` (the common value).
  *
  * The smart constructor enforces:
  *   1. `rna.value.length <= 80` (Rosalind cap);
  *   2. `#A == #U`, else `UnpairedAU`;
  *   3. `#C == #G`, else `UnpairedCG`.
  *
  * The empty RNA string is accepted (it yields `auCount = 0`, `cgCount = 0`,
  * and `PerfectMatching.count` returns `BigInt(1)` — the empty matching).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[PerfectMatchingProblem.from]].
  */
sealed abstract case class PerfectMatchingProblem(
    rna: RnaString,
    auCount: Int,
    cgCount: Int
)

object PerfectMatchingProblem {
  private val MaxLength: Int = 80

  def from(
      rna: RnaString
  ): Either[PerfectMatchingProblemError, PerfectMatchingProblem] = {
    val s   = rna.value
    val len = s.length
    if (len > MaxLength)
      Left(PerfectMatchingProblemError.ExceedsMaxLength(len, MaxLength))
    else {
      val (a, c, g, u) = countSymbols(s)
      if (a != u) Left(PerfectMatchingProblemError.UnpairedAU(a, u))
      else if (c != g) Left(PerfectMatchingProblemError.UnpairedCG(c, g))
      else Right(new PerfectMatchingProblem(rna, a, c) {})
    }
  }

  /** Single-pass count of the four RNA symbols. The input is alphabet-
    * validated by [[RnaString.from]] so the four counters cover everything.
    */
  private def countSymbols(s: String): (Int, Int, Int, Int) = {
    var a = 0
    var c = 0
    var g = 0
    var u = 0
    var i = 0
    while (i < s.length) {
      s.charAt(i) match {
        case 'A' => a += 1
        case 'C' => c += 1
        case 'G' => g += 1
        case 'U' => u += 1
        case _   => () // unreachable: alphabet-validated upstream
      }
      i += 1
    }
    (a, c, g, u)
  }
}
