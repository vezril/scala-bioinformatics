package bio.algorithms.protein

import bio.domain.protein.{AminoAcid, ProteinString, SpectrumMatch, SpectrumMatchProblem}

/** Finds the candidate protein whose complete spectrum best matches a target spectrum
  * — Rosalind PRSM ("Matching a Spectrum to a Protein").
  *
  * The complete spectrum `S[s]` is the multiset of the weights of every prefix and
  * every suffix of `s`. For each candidate the algorithm computes the maximum
  * multiplicity of `R⊖S[s_k]` — the count of the most frequent difference `r − x`
  * (`r ∈ R`, `x ∈ S[s_k]`), i.e. the best single shift aligning the two spectra — and
  * returns the candidate maximising it (ties resolve to the first such candidate).
  *
  * Pure and total: `scanLeft`, comprehensions, and `groupBy`/`maxBy` over immutable
  * vectors, reusing `AminoAcid.monoisotopicMass`. Differences are bucketed to 5 decimals
  * to absorb floating-point error (the CONV idiom).
  */
object MatchSpectrum {

  private val Decimals: Double = 1e5

  def bestMatch(problem: SpectrumMatchProblem): SpectrumMatch = {
    val (multiplicity, protein) =
      problem.proteins
        .map(p => (maxMultiplicity(problem.spectrum, completeSpectrum(p)), p.value))
        .maxBy(_._1)
    SpectrumMatch(multiplicity, protein)
  }

  /** The multiset of all prefix and suffix weights of `p`. */
  private def completeSpectrum(p: ProteinString): Vector[Double] = {
    val masses   = p.value.iterator.flatMap(AminoAcid.fromChar).map(_.monoisotopicMass).toVector
    val prefixes = masses.scanLeft(0.0)(_ + _).tail
    val suffixes = masses.reverse.scanLeft(0.0)(_ + _).tail
    prefixes ++ suffixes
  }

  /** The maximum multiplicity of `R⊖S` — the count of the most frequent difference. */
  private def maxMultiplicity(r: Vector[Double], s: Vector[Double]): Int = {
    val differences = for { a <- r; b <- s } yield round5(a - b)
    differences.groupBy(identity).values.map(_.size).maxOption.getOrElse(0)
  }

  private def round5(d: Double): Double = math.round(d * Decimals) / Decimals
}
