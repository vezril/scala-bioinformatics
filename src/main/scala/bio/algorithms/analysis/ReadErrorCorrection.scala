package bio.algorithms.analysis

import bio.algorithms.nucleic.DnaReverseComplement
import bio.domain.analysis.{Correction, ReadCorrectionProblem, ReadCorrections}
import bio.domain.nucleic.DnaString

/** Identifies single-nucleotide sequencing errors among a set of equal-length
  * DNA reads and returns the corrections (Rosalind CORR — "Error Correction in
  * Reads").
  *
  * A read and its reverse complement represent the same molecule, so support is
  * counted across both strands: a read `r` is **correct** when
  * `support(r) = counts(r) + counts(rc(r)) ≥ 2` (using `counts(r)` alone when
  * `rc(r) == r`), and **incorrect** when `support(r) == 1`. Each correct read
  * contributes both strands (`r` and `rc(r)`) to the set of valid correct forms.
  * Every incorrect read is mapped to the correct form at Hamming distance exactly
  * 1, producing `Correction(read, form)`.
  *
  * Reuses [[DnaReverseComplement]]; Hamming distance is computed inline on the
  * equal-length read strings. The public [[correct]] signature is pure and total.
  */
object ReadErrorCorrection {

  def correct(problem: ReadCorrectionProblem): ReadCorrections = {
    val reads  = problem.reads
    val values = reads.map(_.value)
    val counts = values.groupBy(identity).view.mapValues(_.size).toMap
    val rcOf   = reads.map(d => d.value -> reverseComplement(d)).toMap

    def support(v: String): Int = {
      val rc = rcOf(v)
      counts(v) + (if (rc != v) counts.getOrElse(rc, 0) else 0)
    }

    val correctForms: Set[String] =
      values.distinct.filter(v => support(v) >= 2).flatMap(v => List(v, rcOf(v))).toSet

    val corrections =
      values.filter(v => support(v) == 1).distinct.flatMap { s =>
        correctForms.find(f => hamming(s, f) == 1).map(f => Correction(s, f))
      }

    ReadCorrections(corrections)
  }

  private def reverseComplement(dna: DnaString): String =
    DnaReverseComplement.reverseComplement(dna).value

  /** Hamming distance between two equal-length strings. */
  private def hamming(a: String, b: String): Int =
    a.lazyZip(b).count { case (x, y) => x != y }
}
