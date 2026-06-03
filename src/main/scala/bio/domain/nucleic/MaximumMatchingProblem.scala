package bio.domain.nucleic

/** Validated input bundle for the Rosalind MMCH ("Maximum Matchings and RNA
  * Secondary Structures") problem — see
  * [[bio.algorithms.nucleic.MaximumMatching.count]].
  *
  * Wraps an [[RnaString]] together with its four precomputed symbol counts
  * (`#A`, `#U`, `#C`, `#G`). Unlike [[PerfectMatchingProblem]] (PMCH), MMCH does
  * **not** require the string to be balanced — the unbalanced case is the whole
  * point — so the smart constructor enforces only the length cap (≤ 100). The
  * empty RNA string is accepted (all counts 0, count `1`).
  *
  * Implemented as `sealed abstract case class` so the synthesized `apply` and
  * `copy` cannot leak around the smart constructor — construct via
  * [[MaximumMatchingProblem.from]].
  */
sealed abstract case class MaximumMatchingProblem(
    rna: RnaString,
    aCount: Int,
    uCount: Int,
    cCount: Int,
    gCount: Int
)

object MaximumMatchingProblem {
  private val MaxLength: Int = 100

  def from(
      rna: RnaString
  ): Either[MaximumMatchingProblemError, MaximumMatchingProblem] = {
    val s   = rna.value
    val len = s.length
    if (len > MaxLength)
      Left(MaximumMatchingProblemError.ExceedsMaxLength(len, MaxLength))
    else {
      val (a, u, c, g) = countSymbols(s)
      Right(new MaximumMatchingProblem(rna, a, u, c, g) {})
    }
  }

  /** Single-pass count of the four RNA symbols. The input is alphabet-validated
    * by [[RnaString.from]] so the four counters cover everything.
    */
  private def countSymbols(s: String): (Int, Int, Int, Int) = {
    var a = 0
    var u = 0
    var c = 0
    var g = 0
    var i = 0
    while (i < s.length) {
      s.charAt(i) match {
        case 'A' => a += 1
        case 'U' => u += 1
        case 'C' => c += 1
        case 'G' => g += 1
        case _   => () // unreachable: alphabet-validated upstream
      }
      i += 1
    }
    (a, u, c, g)
  }
}
