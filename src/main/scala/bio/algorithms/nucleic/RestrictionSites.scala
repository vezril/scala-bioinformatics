package bio.algorithms.nucleic

import bio.domain.nucleic.{
  DnaString,
  RestrictionSite,
  RestrictionSiteProblem,
  RestrictionSites => Result
}

/** Locates every reverse palindrome of length 4–12 in a DNA string — Rosalind REVP
  * ("Locating Restriction Sites").
  *
  * A reverse palindrome is a substring equal to its reverse complement; restriction
  * enzymes recognize exactly these sites. Because a reverse palindrome's two central
  * bases must be complementary, its length is always even, so the scan considers
  * lengths `4, 6, 8, 10, 12` only.
  *
  * The result is ordered by `(position, length)` — the `for` comprehension iterates
  * start positions ascending, then lengths ascending. Each candidate substring is a
  * slice of an already-validated `DnaString`, so it is lifted with
  * [[DnaString.unsafeFrom]] without re-validation.
  *
  * Pure and total: no I/O, no mutable state.
  */
object RestrictionSites {

  /** The even palindrome lengths Rosalind REVP requires (4 to 12 inclusive). */
  private val Lengths: Range = 4 to 12 by 2

  def locate(problem: RestrictionSiteProblem): Result = {
    val s = problem.dna.value
    val n = s.length

    val sites =
      for {
        start <- 0 until n
        length <- Lengths
        if start + length <= n
        sub = DnaString.unsafeFrom(s.substring(start, start + length))
        if DnaReverseComplement.reverseComplement(sub).value == sub.value
      } yield RestrictionSite(start + 1, length)

    Result(sites.toVector)
  }
}
