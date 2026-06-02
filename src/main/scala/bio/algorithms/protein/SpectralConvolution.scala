package bio.algorithms.protein

import bio.domain.protein.{
  SpectralConvolution => Result,
  SpectralConvolutionProblem
}

/** Computes the spectral convolution of two mass spectra — Rosalind CONV
  * ("Comparing Spectra with the Spectral Convolution").
  *
  * The spectral convolution `S₁⊖S₂` is the multiset of all differences `s₁ − s₂`.
  * Its largest multiplicity is the maximum number of times any single shift recurs,
  * and the shift achieving it is the offset that best aligns the two spectra (the
  * shared-peaks count for that shift).
  *
  * Each difference is rounded to five decimal places before bucketing so that
  * shifts that are equal in exact decimal arithmetic — but differ by a tiny binary
  * rounding error — are counted together. Among the largest buckets the result is
  * pinned deterministically to the smallest absolute shift (Rosalind accepts any).
  *
  * Pure and total: a `MassMultiset` is always non-empty, so the product of
  * differences is non-empty and a maximal bucket always exists. No I/O, no mutable
  * state.
  */
object SpectralConvolution {

  /** Number of decimal places used to match equal shifts. */
  private val Decimals: Double = 1e5

  private def round5(d: Double): Double = math.round(d * Decimals) / Decimals

  def convolve(problem: SpectralConvolutionProblem): Result = {
    val differences =
      for {
        a <- problem.s1.masses
        b <- problem.s2.masses
      } yield round5(a - b)

    val (shift, multiplicity) = differences
      .groupBy(identity)
      .view
      .mapValues(_.size)
      .toVector
      .sortBy { case (value, count) => (-count, math.abs(value)) }
      .head

    Result(multiplicity, shift)
  }
}
